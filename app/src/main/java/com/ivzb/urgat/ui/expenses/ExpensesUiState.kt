package com.ivzb.urgat.ui.expenses

import com.ivzb.urgat.domain.expense.GroupedExpenseCategories
import com.ivzb.urgat.domain.expense.GroupedExpenses

data class ExpensesUiState(
    val expenses: List<GroupedExpenses> = emptyList(),
    val categories: List<GroupedExpenseCategories> = emptyList(),

    val isLoading: Boolean = false,
    val hasError: Boolean = false,

    val filteringUiInfo: ExpensesFilteringUiInfo = ExpensesFilteringUiInfo()
)

