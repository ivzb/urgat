package com.ivzb.urgat.ui.expenses

import com.ivzb.urgat.data.expense.Expense

data class ExpensesUiState(
    val items: List<Expense> = emptyList(),
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val filteringUiInfo: ExpensesFilteringUiInfo = ExpensesFilteringUiInfo()
)

