package com.ivzb.urgat.ui.finance

import com.ivzb.urgat.data.budget.GroupedBudget
import com.ivzb.urgat.data.fire.FiRe
import com.ivzb.urgat.ui.budgets.BudgetsUiState
import com.ivzb.urgat.ui.components.FilteringUiInfo
import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.components.uiState.NavigationPagerUiState
import com.ivzb.urgat.ui.expenses.ExpensesUiState
import com.ivzb.urgat.ui.investments.InvestmentsUiState
import com.ivzb.urgat.ui.savings.SavingsUiState

data class FinanceUiState(
    override val fabActions: List<FinanceFabAction> = FINANCE_FAB_ACTIONS,

    val expensesUiState: ExpensesUiState = ExpensesUiState(),
    val investmentsUiState: InvestmentsUiState = InvestmentsUiState(),
    val savingsUiState: SavingsUiState = SavingsUiState(),
    val budgetsUiState: BudgetsUiState = BudgetsUiState(),

    val reportBudget: GroupedBudget? = null,
    val fire: FiRe? = null,

    override val isLoading: Boolean = false,
    override val hasError: Boolean = false,
    override val isEmpty: Boolean = false,

    override val pages: List<Page> = FINANCE_PAGES.filter {
        when (it) {
            FinancePage.Report -> reportBudget != null
            FinancePage.FIRE -> fire != null
            else -> true
        }
    },
    override val initialPage: Int = 0,

    override val filteringUiInfo: FinanceFilteringUiInfo = FinanceFilteringUiInfo(),

    val uiEvent: FinanceUiEvent? = null,
): NavigationPagerUiState<FinanceFabActionId> {

    val hasAppliedFilters: Boolean =
        expensesUiState.filteringUiInfo.hasAppliedFilters ||
        investmentsUiState.filteringUiInfo.hasAppliedFilters ||
        savingsUiState.filteringUiInfo.hasAppliedFilters ||
        budgetsUiState.filteringUiInfo.hasAppliedFilters

}