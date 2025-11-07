package com.ivzb.urgat.ui.addEditExpense

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.expense.CreateExpenseUseCase
import com.ivzb.urgat.domain.expense.GetExpenseUseCase
import com.ivzb.urgat.domain.expense.UpdateExpenseUseCase
import com.ivzb.urgat.navigation.AddEditExpenseDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditExpenseViewModel @Inject constructor(
    private val getExpenseUseCase: GetExpenseUseCase,
    private val createExpenseUseCase: CreateExpenseUseCase,
    private val updateExpenseUseCase: UpdateExpenseUseCase,
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

    fun onIntent(intent: AddEditExpenseIntent) {
        when (intent) {
            is AddEditExpenseIntent.UpdateName -> updateName(intent.value)
            is AddEditExpenseIntent.UpdateAmount -> updateAmount(intent.value)
            is AddEditExpenseIntent.UpdateCategory -> updateCategory(intent.value)
            is AddEditExpenseIntent.UpdateDate -> updateDate(intent.value)
            AddEditExpenseIntent.SaveExpense -> saveExpense()
            AddEditExpenseIntent.UserMessageShown -> userMessageShown()
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

    private fun updateCategory(category: String) {
        _uiState.update {
            it.copy(category = category)
        }
    }

    private fun updateDate(date: Long) {
        _uiState.update {
            it.copy(date = date)
        }
    }

    private fun saveExpense() {
        val fields = listOf(
            uiState.value.name,
            uiState.value.amount,
            uiState.value.category,
        )

        val emptyIndex = fields.indexOfFirst { it.isEmpty() }

        if (emptyIndex != -1) {
            _uiState.update {
                it.copy(
                    focusIndex = emptyIndex,
                    userMessage = R.string.empty_expense_message
                )
            }

            return
        }

        if (expenseId == null) {
            createNewExpense()
        } else {
            updateExpense()
        }
    }

    private fun createNewExpense() = viewModelScope.launch {
        createExpenseUseCase(
            name = uiState.value.name,
            amount = uiState.value.amount.toBigDecimal(),
            category = uiState.value.category,
            date = uiState.value.date
        )

        _uiState.update {
            it.copy(isExpenseSaved = true)
        }
    }


    private fun userMessageShown() {
        _uiState.update {
            it.copy(userMessage = null)
        }
    }

    private fun updateExpense() {
        if (expenseId == null) {
            throw RuntimeException("updateExpense() was called but expense is new.")
        }

        viewModelScope.launch {
            updateExpenseUseCase(
                expenseId,
                name = uiState.value.name,
                amount = uiState.value.amount.toBigDecimal(),
                category = uiState.value.category,
                date = uiState.value.date
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
            getExpenseUseCase(expenseId).let { expense ->
                if (expense != null) {
                    _uiState.update {
                        it.copy(
                            name = expense.name,
                            amount = expense.amount.toString(),
                            category = expense.category,
                            date = expense.date,
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