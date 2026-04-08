package com.ivzb.urgat.ui.addEditExpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.expense.CreateExpenseUseCase
import com.ivzb.urgat.domain.expense.DeleteExpenseUseCase
import com.ivzb.urgat.domain.expense.GetExpenseUseCase
import com.ivzb.urgat.domain.expense.UpdateExpenseUseCase
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

@HiltViewModel(assistedFactory = AddEditExpenseViewModel.Factory::class)
class AddEditExpenseViewModel @AssistedInject constructor(
    @Assisted val route: AddEditExpenseRoute,
    private val getExpenseUseCase: GetExpenseUseCase,
    private val createExpenseUseCase: CreateExpenseUseCase,
    private val updateExpenseUseCase: UpdateExpenseUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
): ViewModel() {

    private val screenTitleResId: Int = route.titleResId
    private val expenseId: String? = route.id

    private val _uiState = MutableStateFlow(AddEditExpenseUiState(
        screenTitleResId = screenTitleResId,
        withDuplicate = expenseId != null,
    ))
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
            is AddEditExpenseIntent.UpdateCurrency -> updateCurrency(intent.value)
            is AddEditExpenseIntent.UpdateCategory -> updateCategory(intent.value)
            is AddEditExpenseIntent.UpdateDate -> updateDate(intent.value)
            is AddEditExpenseIntent.UpdateExcludeFromAnalytics -> updateExcludeFromAnalytics(intent.value)
            AddEditExpenseIntent.SaveExpense -> saveExpense()
            AddEditExpenseIntent.DeleteExpense -> deleteExpense()
            AddEditExpenseIntent.DuplicateExpense -> duplicateExpense()
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

    private fun updateCurrency(currency: String) {
        _uiState.update {
            it.copy(currency = currency)
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

    private fun updateExcludeFromAnalytics(excludeFromAnalytics: Boolean) {
        _uiState.update {
            it.copy(excludeFromAnalytics = excludeFromAnalytics)
        }
    }

    private fun saveExpense() {
        val fields = listOf(
            uiState.value.name,
            uiState.value.amount,
            uiState.value.currency,
            uiState.value.category,
        )

        val emptyIndex = fields.indexOfFirst { it.sanitize().isEmpty() }

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
            name = uiState.value.name.sanitize(),
            amount = uiState.value.amount.sanitize().toBigDecimal(),
            currency = uiState.value.currency.sanitize(),
            category = uiState.value.category.sanitize(),
            date = uiState.value.date,
            excludeFromAnalytics = uiState.value.excludeFromAnalytics,
        )

        _uiState.update {
            it.copy(shouldNavigateBack = true)
        }
    }

    private fun deleteExpense() {
        if (expenseId == null) {
            Assert.fail("deleteExpense() was called but id is null.")

            _uiState.update {
                it.copy(userMessage = R.string.something_went_wrong)
            }

            return
        }

        viewModelScope.launch {
            deleteExpenseUseCase(expenseId)

            _uiState.update {
                it.copy(shouldNavigateBack = true)
            }
        }
    }

    private fun duplicateExpense() {
        if (expenseId == null) {
            Assert.fail("duplicateExpense() was called but id is null.")

            _uiState.update {
                it.copy(userMessage = R.string.something_went_wrong)
            }

            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(date = System.currentTimeMillis())
            }

            createNewExpense()
        }
    }

    private fun updateExpense() {
        if (expenseId == null) {
            Assert.fail("updateExpense() was called but id is null.")

            _uiState.update {
                it.copy(userMessage = R.string.something_went_wrong)
            }

            return
        }

        viewModelScope.launch {
            updateExpenseUseCase(
                expenseId,
                name = uiState.value.name.sanitize(),
                amount = uiState.value.amount.sanitize().toBigDecimal(),
                currency = uiState.value.currency.sanitize(),
                category = uiState.value.category.sanitize(),
                date = uiState.value.date,
                excludeFromAnalytics = uiState.value.excludeFromAnalytics,
            )

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
                            currency = expense.currency,
                            category = expense.category,
                            date = expense.date,
                            excludeFromAnalytics = expense.excludeFromAnalytics,
                            isLoading = false,
                            withDelete = true,
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
        fun create(route: AddEditExpenseRoute): AddEditExpenseViewModel
    }

}