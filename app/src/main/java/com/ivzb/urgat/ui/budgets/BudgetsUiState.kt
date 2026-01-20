package com.ivzb.urgat.ui.budgets

import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.domain.budget.GroupedBudgetCategories
import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.components.uiState.PagerUiState
import java.math.BigDecimal

data class BudgetsUiState(
    val budgets: List<Budget> = emptyList(),
    val monthlyBudgets: List<MonthlyBudgetUiState> = listOf(),

    override val isLoading: Boolean = false,
    override val hasError: Boolean = false,
    override val isEmpty: Boolean = budgets.isEmpty(),

    override val pages: List<Page> = BUDGET_PAGES,
    override val initialPage: Int = 0,

    override val filteringUiInfo: BudgetsFilteringUiInfo = BudgetsFilteringUiInfo(),
): PagerUiState

data class MonthlyBudgetUiState(
    val category: GroupedBudgetCategories,
    val totalIncome: BigDecimal,
    val totalExpenses: BigDecimal,
    val totalInvestments: BigDecimal,
    val totalInvestmentsFormatted: String,
) {

    val remaining: BigDecimal
        get() = totalIncome - totalExpenses - totalIncome

    val total: BigDecimal
        get() = totalIncome.coerceAtLeast(totalExpenses + totalInvestments)

}