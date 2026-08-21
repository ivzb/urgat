package com.ivzb.urgat.ui.expenses

import com.ivzb.urgat.data.expense.ExpenseAverage
import com.ivzb.urgat.data.expense.ExpenseCategory
import com.ivzb.urgat.domain.expense.GroupedExpenseCategories
import com.ivzb.urgat.domain.expense.GroupedExpenses
import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.components.uiState.PagerUiState

data class ExpensesUiState(
    val expenses: List<GroupedExpenses> = listOf(),
    val categoriesMonthly: List<GroupedExpenseCategories> = listOf(),
    val categoriesYearly: List<GroupedExpenseCategories> = listOf(),
    val categoriesAllTime: List<ExpenseCategory> = listOf(),

    val averagesMonthly: Map<String, ExpenseAverage> = mapOf(),
    val averagesYearly: Map<String, ExpenseAverage> = mapOf(),
    val averageAllTime: ExpenseAverage = ExpenseAverage.EMPTY,

    override val isLoading: Boolean = false,
    override val hasError: Boolean = false,
    override val isEmpty: Boolean = expenses.isEmpty(),

    override val pages: List<Page> = EXPENSE_PAGES,
    override val initialPage: Int = 0,

    override val filteringUiInfo: ExpensesFilteringUiInfo = ExpensesFilteringUiInfo()
): PagerUiState
