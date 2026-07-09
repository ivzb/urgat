package com.ivzb.urgat.ui.addEditTrade

import kotlin.text.isEmpty
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.trade.CreateTradeUseCase
import com.ivzb.urgat.domain.trade.DeleteTradeUseCase
import com.ivzb.urgat.domain.trade.GetTradeUseCase
import com.ivzb.urgat.domain.trade.GetLastUsedTradeCurrencyUseCase
import com.ivzb.urgat.domain.trade.GetLastUsedTradeFeeUseCase
import com.ivzb.urgat.domain.trade.GetLastUsedTradeNameUseCase
import com.ivzb.urgat.domain.trade.UpdateTradeUseCase
import com.ivzb.urgat.util.Assert
import com.ivzb.urgat.util.sanitize
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = AddEditTradeViewModel.Factory::class)
class AddEditTradeViewModel @AssistedInject constructor(
    @Assisted val route: AddEditTradeRoute,
    private val getTradeUseCase: GetTradeUseCase,
    private val getLastUsedTradeCurrencyUseCase: GetLastUsedTradeCurrencyUseCase,
    private val getLastUsedTradeNameUseCase: GetLastUsedTradeNameUseCase,
    private val getLastUsedTradeFeeUseCase: GetLastUsedTradeFeeUseCase,
    private val createTradeUseCase: CreateTradeUseCase,
    private val updateTradeUseCase: UpdateTradeUseCase,
    private val deleteTradeUseCase: DeleteTradeUseCase,
): ViewModel() {

    private val screenTitleResId: Int = route.titleResId
    private var tradeId: String? = route.id

    private val _uiState = MutableStateFlow(AddEditTradeUiState(
        screenTitleResId = screenTitleResId,
        withDuplicate = tradeId != null,
    ))
    val uiState: StateFlow<AddEditTradeUiState> = _uiState.asStateFlow()

    init {
        val id = tradeId

        if (id != null) {
            loadTrade(id)
        } else {
            loadLastUsedData()
        }
    }

    fun onIntent(intent: AddEditTradeIntent) {
        when (intent) {
            is AddEditTradeIntent.UpdateName -> updateName(intent.value)
            is AddEditTradeIntent.UpdateShares -> updateShares(intent.value)
            is AddEditTradeIntent.UpdateFee -> updateFee(intent.value)
            is AddEditTradeIntent.UpdateMarketPrice -> updateMarketPrice(intent.value)
            is AddEditTradeIntent.UpdateCurrency -> updateCurrency(intent.value)
            is AddEditTradeIntent.UpdateOrderType -> updateOrderType(intent.value)
            is AddEditTradeIntent.UpdateDate -> updateDate(intent.value)
            AddEditTradeIntent.SaveTrade -> saveTrade()
            AddEditTradeIntent.DeleteTrade -> deleteTrade()
            AddEditTradeIntent.DuplicateTrade -> duplicateTrade()
            AddEditTradeIntent.UserMessageShown -> userMessageShown()
        }
    }

    private fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    private fun updateShares(shares: String) {
        _uiState.update { it.copy(shares = shares) }
    }

    private fun updateFee(fee: String) {
        _uiState.update { it.copy(fee = fee) }
    }

    private fun updateMarketPrice(marketPrice: String) {
        _uiState.update { it.copy(marketPrice = marketPrice) }
    }

    private fun updateCurrency(currency: String) {
        _uiState.update { it.copy(currency = currency) }
    }

    private fun updateOrderType(orderType: String) {
        _uiState.update { it.copy(orderType = orderType) }
    }

    private fun updateDate(date: Long) {
        _uiState.update { it.copy(date = date) }
    }

    private fun saveTrade() {
        val fields = listOf(
            uiState.value.name,
            uiState.value.shares,
            uiState.value.fee,
            uiState.value.marketPrice,
            uiState.value.currency,
            uiState.value.orderType,
        )

        val emptyIndex = fields.indexOfFirst { it.sanitize().isEmpty() }

        if (emptyIndex != -1) {
            _uiState.update {
                it.copy(
                    focusIndex = emptyIndex,
                    userMessage = R.string.empty_trade_message
                )
            }

            return
        }

        if (tradeId == null) {
            createNewTrade()
        } else {
            updateTrade()
        }
    }

    private fun createNewTrade() = viewModelScope.launch {
        createTradeUseCase(
            name = uiState.value.name.sanitize(),
            shares = uiState.value.shares.sanitize().toBigDecimal(),
            fee = uiState.value.fee.sanitize().toBigDecimal(),
            marketPrice = uiState.value.marketPrice.sanitize().toBigDecimal(),
            currency = uiState.value.currency.sanitize(),
            orderType = uiState.value.orderType.sanitize(),
            date = uiState.value.date,
        )

        _uiState.update {
            it.copy(shouldNavigateBack = true)
        }
    }

    private fun updateTrade() {
        val id = tradeId

        if (id == null) {
            Assert.fail("updateTrade() was called but id is null.")

            _uiState.update {
                it.copy(userMessage = R.string.something_went_wrong)
            }

            return
        }

        viewModelScope.launch {
            updateTradeUseCase(
                id,
                name = uiState.value.name.sanitize(),
                shares = uiState.value.shares.sanitize().toBigDecimal(),
                fee = uiState.value.fee.sanitize().toBigDecimal(),
                marketPrice = uiState.value.marketPrice.sanitize().toBigDecimal(),
                currency = uiState.value.currency.sanitize(),
                orderType = uiState.value.orderType.sanitize(),
                date = uiState.value.date,
            )

            _uiState.update {
                it.copy(shouldNavigateBack = true)
            }
        }
    }

    private fun deleteTrade() {
        val id = tradeId

        if (id == null) {
            Assert.fail("deleteTrade() was called but id is null.")

            _uiState.update {
                it.copy(userMessage = R.string.something_went_wrong)
            }

            return
        }

        viewModelScope.launch {
            deleteTradeUseCase(id)

            _uiState.update {
                it.copy(shouldNavigateBack = true)
            }
        }
    }

    private fun duplicateTrade() {
        if (tradeId == null) {
            Assert.fail("duplicateTrade() was called but id is null.")

            _uiState.update {
                it.copy(userMessage = R.string.something_went_wrong)
            }

            return
        }

        tradeId = null

        _uiState.update {
            it.copy(
                screenTitleResId = R.string.add_trade,
                date = System.currentTimeMillis(),
                withDelete = false,
                withDuplicate = false,
            )
        }
    }

    private fun userMessageShown() {
        _uiState.update {
            it.copy(userMessage = null)
        }
    }

    private fun loadLastUsedData() {
        viewModelScope.launch {
            val name = getLastUsedTradeNameUseCase()
            val fee = getLastUsedTradeFeeUseCase()
            val currency = getLastUsedTradeCurrencyUseCase()

            _uiState.update {
                it.copy(
                    name = name ?: it.name,
                    fee = fee ?: it.fee,
                    currency = currency ?: it.currency,
                )
            }
        }
    }

    private fun loadTrade(tradeId: String) {
        _uiState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {
            getTradeUseCase(tradeId).let { trade ->
                if (trade != null) {
                    _uiState.update {
                        it.copy(
                            name = trade.name,
                            shares = trade.shares.toString(),
                            marketPrice = trade.marketPrice.toString(),
                            fee = trade.fee.toString(),
                            currency = trade.currency,
                            orderType = trade.orderType,
                            date = trade.date,
                            isLoading = false,
                            withDelete = true
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(route: AddEditTradeRoute): AddEditTradeViewModel
    }

}

