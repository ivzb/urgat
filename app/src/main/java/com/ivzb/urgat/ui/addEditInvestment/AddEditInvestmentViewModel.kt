package com.ivzb.urgat.ui.addEditInvestment

import kotlin.text.isEmpty
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.investment.CreateInvestmentUseCase
import com.ivzb.urgat.domain.investment.DeleteInvestmentUseCase
import com.ivzb.urgat.domain.investment.GetInvestmentUseCase
import com.ivzb.urgat.domain.investment.UpdateInvestmentUseCase
import com.ivzb.urgat.navigation.AddEditInvestmentDestination
import com.ivzb.urgat.util.Assert
import com.ivzb.urgat.util.sanitize
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditInvestmentViewModel @Inject constructor(
    private val getInvestmentUseCase: GetInvestmentUseCase,
    private val createInvestmentUseCase: CreateInvestmentUseCase,
    private val updateInvestmentUseCase: UpdateInvestmentUseCase,
    private val deleteInvestmentUseCase: DeleteInvestmentUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val investmentId: String? = savedStateHandle[AddEditInvestmentDestination.Args.ARG_ID]

    private val _uiState = MutableStateFlow(AddEditInvestmentUiState())
    val uiState: StateFlow<AddEditInvestmentUiState> = _uiState.asStateFlow()

    init {
        if (investmentId != null) {
            loadInvestment(investmentId)
        }
    }

    fun onIntent(intent: AddEditInvestmentIntent) {
        when (intent) {
            is AddEditInvestmentIntent.UpdateName -> updateName(intent.value)
            is AddEditInvestmentIntent.UpdateAmount -> updateAmount(intent.value)
            is AddEditInvestmentIntent.UpdateCost -> updateCost(intent.value)
            is AddEditInvestmentIntent.UpdateCategory -> updateCategory(intent.value)
            is AddEditInvestmentIntent.UpdateCurrency -> updateCurrency(intent.value)
            is AddEditInvestmentIntent.UpdateDate -> updateDate(intent.value)
            AddEditInvestmentIntent.SaveInvestment -> saveInvestment()
            AddEditInvestmentIntent.DeleteInvestment -> deleteInvestment()
            AddEditInvestmentIntent.UserMessageShown -> userMessageShown()
        }
    }

    private fun updateName(name: String) {
        _uiState.update {
            it.copy(name = name)
        }
    }

    private fun updateAmount(amount: String) {
        _uiState.update {
            it.copy(amount = amount)
        }
    }

    private fun updateCost(cost: String) {
        _uiState.update {
            it.copy(cost = cost)
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
            uiState.value.amount,
            uiState.value.cost,
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
            amount = uiState.value.amount.sanitize().toBigDecimal(),
            cost = uiState.value.cost.sanitize().toBigDecimal(),
            category = uiState.value.category.sanitize(),
            currency = uiState.value.currency.sanitize(),
            date = uiState.value.date,
        )

        _uiState.update {
            it.copy(shouldNavigateBack = true)
        }
    }

    private fun updateInvestment() {
        if (investmentId == null) {
            Assert.fail("updateInvestment() was called but id is null.")

            _uiState.update {
                it.copy(userMessage = R.string.something_went_wrong)
            }

            return
        }

        viewModelScope.launch {
            updateInvestmentUseCase(
                investmentId,
                name = uiState.value.name.sanitize(),
                amount = uiState.value.amount.sanitize().toBigDecimal(),
                cost = uiState.value.cost.sanitize().toBigDecimal(),
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
        if (investmentId == null) {
            Assert.fail("deleteInvestment() was called but id is null.")

            _uiState.update {
                it.copy(userMessage = R.string.something_went_wrong)
            }

            return
        }

        viewModelScope.launch {
            deleteInvestmentUseCase(investmentId)

            _uiState.update {
                it.copy(shouldNavigateBack = true)
            }
        }
    }

    private fun userMessageShown() {
        _uiState.update {
            it.copy(userMessage = null)
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
                            amount = investment.amount.toString(),
                            cost = investment.cost.toString(),
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

}
