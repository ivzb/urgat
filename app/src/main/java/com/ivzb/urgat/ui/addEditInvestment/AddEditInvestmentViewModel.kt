package com.ivzb.urgat.ui.addEditInvestment

import kotlin.text.isEmpty
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.investment.CreateInvestmentUseCase
import com.ivzb.urgat.domain.investment.GetInvestmentUseCase
import com.ivzb.urgat.domain.investment.UpdateInvestmentUseCase
import com.ivzb.urgat.navigation.AddEditInvestmentDestination
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
            is AddEditInvestmentIntent.UpdateCurrency -> updateCurrency(intent.value)
            is AddEditInvestmentIntent.UpdateAmount -> updateAmount(intent.value)
            is AddEditInvestmentIntent.UpdateCategory -> updateCategory(intent.value)
            AddEditInvestmentIntent.SaveInvestment -> saveInvestment()
            AddEditInvestmentIntent.UserMessageShown -> userMessageShown()
        }
    }

    private fun updateName(name: String) {
        _uiState.update {
            it.copy(name = name)
        }
    }

    private fun updateCurrency(currency: String) {
        _uiState.update {
            it.copy(currency = currency)
        }
    }

    private fun updateAmount(amount: String) {
        _uiState.update {
            it.copy(amount = amount)
        }
    }

    private fun updateCategory(category: String) {
        _uiState.update {
            it.copy(category = category)
        }
    }

    private fun saveInvestment() {
        val fields = listOf(
            uiState.value.name,
            uiState.value.currency,
            uiState.value.amount,
            uiState.value.category,
        )

        val emptyIndex = fields.indexOfFirst { it.isEmpty() }

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
            name = uiState.value.name,
            currency = uiState.value.currency,
            amount = uiState.value.amount.toBigDecimal(),
            category = uiState.value.category,
        )

        _uiState.update {
            it.copy(isInvestmentSaved = true)
        }
    }


    private fun userMessageShown() {
        _uiState.update {
            it.copy(userMessage = null)
        }
    }

    private fun updateInvestment() {
        if (investmentId == null) {
            throw RuntimeException("updateInvestment() was called but investment is new.")
        }

        viewModelScope.launch {
            updateInvestmentUseCase(
                investmentId,
                name = uiState.value.name,
                currency = uiState.value.currency,
                amount = uiState.value.amount.toBigDecimal(),
                category = uiState.value.category,
            )

            _uiState.update {
                it.copy(isInvestmentSaved = true)
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
                            currency = investment.currency,
                            amount = investment.amount.toString(),
                            category = investment.category,
                            isLoading = false,
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
