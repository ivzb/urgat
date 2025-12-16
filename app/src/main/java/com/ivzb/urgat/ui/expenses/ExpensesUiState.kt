package com.ivzb.urgat.ui.expenses

import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.ui.GroupedItems

data class ExpensesUiState(
    val items: List<GroupedItems<String, Expense>> = emptyList(),

    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val filteringUiInfo: ExpensesFilteringUiInfo = ExpensesFilteringUiInfo()
)

