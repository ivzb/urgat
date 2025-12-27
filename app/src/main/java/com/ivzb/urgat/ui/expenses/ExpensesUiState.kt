package com.ivzb.urgat.ui.expenses

import com.ivzb.urgat.domain.expense.GroupedExpenses

data class ExpensesUiState(
    val items: List<GroupedExpenses> = emptyList(),

    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val filteringUiInfo: ExpensesFilteringUiInfo = ExpensesFilteringUiInfo()
)

