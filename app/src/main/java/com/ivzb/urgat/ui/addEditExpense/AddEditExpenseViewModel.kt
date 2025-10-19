package com.ivzb.urgat.ui.addEditExpense

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.data.expense.ExpensesRepository
import com.ivzb.urgat.navigation.AddEditExpenseDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

data class AddEditExpenseUiState(
    val name: String = "",
    val amount: String = "",
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val isExpenseSaved: Boolean = false
)

@HiltViewModel
class AddEditExpenseViewModel @Inject constructor(
    private val expensesRepository: ExpensesRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val expenseId: String? = savedStateHandle[AddEditExpenseDestination.Args.ARG_ID]

    private val _uiState = MutableStateFlow(AddEditExpenseUiState())
    val uiState: StateFlow<AddEditExpenseUiState> = _uiState.asStateFlow()

    init {
        if (expenseId != null) {
            loadExpense(expenseId)
        }
    }

    fun saveExpense() {
        if (uiState.value.name.isNotEmpty() && uiState.value.amount.isNotEmpty()) {
            _uiState.update {
                it.copy(userMessage = R.string.empty_expense_message)
            }

            return
        }

        if (expenseId == null) {
            createNewExpense()
        } else {
            updateExpense()
        }
    }

    fun snackbarMessageShown() {
        _uiState.update {
            it.copy(userMessage = null)
        }
    }

    fun updateName(name: String) {
        _uiState.update {
            it.copy(name = name)
        }
    }

    fun updateAmount(amount: BigDecimal) {
        _uiState.update {
            it.copy(amount = amount.toString())
        }
    }

    private fun createNewExpense() = viewModelScope.launch {
        expensesRepository.create(
            name = uiState.value.name,
            amount = uiState.value.amount.toBigDecimal()
        )

        _uiState.update {
            it.copy(isExpenseSaved = true)
        }
    }

    private fun updateExpense() {
        if (expenseId == null) {
            throw RuntimeException("updateExpense() was called but expense is new.")
        }

        viewModelScope.launch {
            expensesRepository.update(
                expenseId,
                name = uiState.value.name,
                amount = uiState.value.amount.toBigDecimal(),
            )

            _uiState.update {
                it.copy(isExpenseSaved = true)
            }
        }
    }

    private fun loadExpense(expenseId: String) {
        _uiState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {
            expensesRepository.get(expenseId).let { expense ->
                if (expense != null) {
                    _uiState.update {
                        it.copy(
                            name = expense.name,
                            amount = expense.amount.toString(),
                            isLoading = false
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