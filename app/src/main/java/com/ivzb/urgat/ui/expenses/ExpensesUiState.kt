package com.ivzb.urgat.ui.expenses

import com.ivzb.urgat.data.expense.ExpenseCategory
import com.ivzb.urgat.domain.expense.GroupedExpenseCategories
import com.ivzb.urgat.domain.expense.GroupedExpenses
import com.ivzb.urgat.ui.components.screens.UiState

data class ExpensesUiState(
    val expenses: List<GroupedExpenses> = emptyList(),
    val categoriesMonthly: List<GroupedExpenseCategories> = emptyList(),
    val categoriesYearly: List<GroupedExpenseCategories> = emptyList(),
    val categoriesAllTime: List<ExpenseCategory> = emptyList(),

    override val isLoading: Boolean = false,
    override val hasError: Boolean = false,
    override val isEmpty: Boolean = expenses.isEmpty(),

    val initialPage: Int = 0,

    override val filteringUiInfo: ExpensesFilteringUiInfo = ExpensesFilteringUiInfo()
): UiState
