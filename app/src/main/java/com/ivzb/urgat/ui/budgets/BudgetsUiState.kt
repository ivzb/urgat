package com.ivzb.urgat.ui.budgets

import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.domain.budget.GroupedBudgetCategories
import com.ivzb.urgat.domain.expense.GroupedExpenses
import com.ivzb.urgat.domain.investment.GroupedInvestments
import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.components.uiState.PagerUiState

data class BudgetsUiState(
    val budgets: List<Budget> = emptyList(),
    val budgetCategoriesMonthly: List<GroupedBudgetCategories> = emptyList(),
    val expensesMonthly: List<GroupedExpenses> = emptyList(),
    val investmentsMonthly: List<GroupedInvestments> = emptyList(),

    override val isLoading: Boolean = false,
    override val hasError: Boolean = false,
    override val isEmpty: Boolean = budgets.isEmpty(),

    override val pages: List<Page> = BUDGET_PAGES,
    override val initialPage: Int = 0,

    override val filteringUiInfo: BudgetsFilteringUiInfo = BudgetsFilteringUiInfo(),
): PagerUiState