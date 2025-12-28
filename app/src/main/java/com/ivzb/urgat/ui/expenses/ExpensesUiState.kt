package com.ivzb.urgat.ui.expenses

import com.ivzb.urgat.data.expense.ExpenseCategory
import com.ivzb.urgat.domain.expense.GroupedExpenseCategories
import com.ivzb.urgat.domain.expense.GroupedExpenses

data class ExpensesUiState(
    val expenses: List<GroupedExpenses> = emptyList(),
    val categoriesMonthly: List<GroupedExpenseCategories> = emptyList(),
    val categoriesYearly: List<GroupedExpenseCategories> = emptyList(),
    val categoriesAllTime: List<ExpenseCategory> = emptyList(),

    val isLoading: Boolean = false,
    val hasError: Boolean = false,

    val filteringUiInfo: ExpensesFilteringUiInfo = ExpensesFilteringUiInfo()
)

