package com.ivzb.urgat.ui.addEditInvestment

import kotlin.text.isEmpty
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.investment.CreateInvestmentUseCase
import com.ivzb.urgat.domain.investment.DeleteInvestmentUseCase
import com.ivzb.urgat.domain.investment.GetInvestmentUseCase
import com.ivzb.urgat.domain.investment.GetLastUsedInvestmentCurrencyUseCase
import com.ivzb.urgat.domain.investment.UpdateInvestmentUseCase
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

@HiltViewModel(assistedFactory = AddEditInvestmentViewModel.Factory::class)
class AddEditInvestmentViewModel @AssistedInject constructor(
    @Assisted val route: AddEditInvestmentRoute,
    private val getInvestmentUseCase: GetInvestmentUseCase,
    private val getLastUsedInvestmentCurrencyUseCase: GetLastUsedInvestmentCurrencyUseCase,
    private val createInvestmentUseCase: CreateInvestmentUseCase,
    private val updateInvestmentUseCase: UpdateInvestmentUseCase,
    private val deleteInvestmentUseCase: DeleteInvestmentUseCase,
): ViewModel() {

    private val screenTitleResId: Int = route.titleResId
    private var investmentId: String? = route.id

    private val _uiState = MutableStateFlow(AddEditInvestmentUiState(
        screenTitleResId = screenTitleResId,
        withDuplicate = investmentId != null,
    ))
    val uiState: StateFlow<AddEditInvestmentUiState> = _uiState.asStateFlow()

    init {
        val id = investmentId

        if (id != null) {
            loadInvestment(id)
        } else {
            loadLastUsedCurrency()
        }
    }

    fun onIntent(intent: AddEditInvestmentIntent) {
        when (intent) {
            is AddEditInvestmentIntent.UpdateName -> updateName(intent.value)
            is AddEditInvestmentIntent.UpdateShares -> updateShares(intent.value)
            is AddEditInvestmentIntent.UpdateCostPrice -> updateCostPrice(intent.value)
            is AddEditInvestmentIntent.UpdateCategory -> updateCategory(intent.value)
            is AddEditInvestmentIntent.UpdateCurrency -> updateCurrency(intent.value)
            is AddEditInvestmentIntent.UpdateDate -> updateDate(intent.value)
            AddEditInvestmentIntent.SaveInvestment -> saveInvestment()
            AddEditInvestmentIntent.DeleteInvestment -> deleteInvestment()
            AddEditInvestmentIntent.DuplicateInvestment -> duplicateInvestment()
            AddEditInvestmentIntent.UserMessageShown -> userMessageShown()
        }
    }

    private fun updateName(name: String) {
        _uiState.update {
            it.copy(name = name)
        }
    }

    private fun updateShares(shares: String) {
        _uiState.update {
            it.copy(shares = shares)
        }
    }

    private fun updateCostPrice(costPrice: String) {
        _uiState.update {
            it.copy(costPrice = costPrice)
        }
    }

    private fun updateCategory(category: String) {
        _uiState.update {
            it.copy(category = category)
        }
    }

    private fun updateCurrency(currency: String) {
        _uiState.update {
            it.copy(currency = currency)
        }
    }

    private fun updateDate(date: Long) {
        _uiState.update {
            it.copy(date = date)
        }
    }

    private fun saveInvestment() {
        val fields = listOf(
            uiState.value.name,
            uiState.value.shares,
            uiState.value.costPrice,
            uiState.value.category,
            uiState.value.currency,
        )

        val emptyIndex = fields.indexOfFirst { it.sanitize().isEmpty() }

        if (emptyIndex != -1) {
            _uiState.update {
                it.copy(
                    focusIndex = emptyIndex,
                    userMessage = R.string.empty_investment_message
                )
            }

            return
        }

        if (investmentId == null) {
            createNewInvestment()
        } else {
            updateInvestment()
        }
    }

    private fun createNewInvestment() = viewModelScope.launch {
        createInvestmentUseCase(
            name = uiState.value.name.sanitize(),
            shares = uiState.value.shares.sanitize().toBigDecimal(),
            costPrice = uiState.value.costPrice.sanitize().toBigDecimal(),
            category = uiState.value.category.sanitize(),
            currency = uiState.value.currency.sanitize(),
            date = uiState.value.date,
        )

        _uiState.update {
            it.copy(shouldNavigateBack = true)
        }
    }

    private fun updateInvestment() {
        val id = investmentId

        if (id == null) {
            Assert.fail("updateInvestment() was called but id is null.")

            _uiState.update {
                it.copy(userMessage = R.string.something_went_wrong)
            }

            return
        }

        viewModelScope.launch {
            updateInvestmentUseCase(
                id,
                name = uiState.value.name.sanitize(),
                shares = uiState.value.shares.sanitize().toBigDecimal(),
                costPrice = uiState.value.costPrice.sanitize().toBigDecimal(),
                category = uiState.value.category.sanitize(),
                currency = uiState.value.currency.sanitize(),
                date = uiState.value.date,
            )

            _uiState.update {
                it.copy(shouldNavigateBack = true)
            }
        }
    }

    private fun deleteInvestment() {
        val id = investmentId

        if (id == null) {
            Assert.fail("deleteInvestment() was called but id is null.")

            _uiState.update {
                it.copy(userMessage = R.string.something_went_wrong)
            }

            return
        }

        viewModelScope.launch {
            deleteInvestmentUseCase(id)

            _uiState.update {
                it.copy(shouldNavigateBack = true)
            }
        }
    }

    private fun duplicateInvestment() {
        if (investmentId == null) {
            Assert.fail("duplicateInvestment() was called but id is null.")

            _uiState.update {
                it.copy(userMessage = R.string.something_went_wrong)
            }

            return
        }

        investmentId = null

        _uiState.update {
            it.copy(
                screenTitleResId = R.string.add_investment,
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

    private fun loadLastUsedCurrency() {
        viewModelScope.launch {
            val currency = getLastUsedInvestmentCurrencyUseCase()

            if (currency != null) {
                _uiState.update {
                    it.copy(currency = currency)
                }
            }
        }
    }

    private fun loadInvestment(investmentId: String) {
        _uiState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {
            getInvestmentUseCase(investmentId).let { investment ->
                if (investment != null) {
                    _uiState.update {
                        it.copy(
                            name = investment.name,
                            shares = investment.shares.toString(),
                            costPrice = investment.costPrice.toString(),
                            category = investment.category,
                            currency = investment.currency,
                            date = investment.date,
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
        fun create(route: AddEditInvestmentRoute): AddEditInvestmentViewModel
    }

}
