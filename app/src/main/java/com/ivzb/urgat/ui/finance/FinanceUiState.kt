package com.ivzb.urgat.ui.finance

import com.ivzb.urgat.data.budget.GroupedBudget
import com.ivzb.urgat.data.fire.FiRe
import com.ivzb.urgat.ui.budgets.BudgetsUiState
import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.components.uiState.PagerUiState
import com.ivzb.urgat.ui.expenses.ExpensesUiState
import com.ivzb.urgat.ui.fire.FireDateRangeType
import com.ivzb.urgat.ui.investments.InvestmentsUiState
import com.ivzb.urgat.ui.savings.SavingsUiState
import com.ivzb.urgat.ui.trades.TradesUiState
import com.ivzb.urgat.ui.watchlist.WatchlistAssetsUiState

data class FinanceUiState(
    val expensesUiState: ExpensesUiState = ExpensesUiState(),
    val investmentsUiState: InvestmentsUiState = InvestmentsUiState(),
    val tradesUiState: TradesUiState = TradesUiState(),
    val savingsUiState: SavingsUiState = SavingsUiState(),
    val budgetsUiState: BudgetsUiState = BudgetsUiState(),
    val watchlistAssetsUiState: WatchlistAssetsUiState = WatchlistAssetsUiState(),

    val reportBudget: GroupedBudget? = null,
    val fire: FiRe? = null,
    val fireDateRangeType: FireDateRangeType = FireDateRangeType.LAST_MONTH,
    val fireCustomStart: Long = 0L,
    val fireCustomEnd: Long = 0L,

    override val isLoading: Boolean = false,
    override val hasError: Boolean = false,
    override val isEmpty: Boolean = false,

    override val pages: List<Page> = FINANCE_PAGES.filter {
        when (it) {
            FinancePage.Report -> reportBudget != null
            else -> true
        }
    },
    override val initialPage: Int = 0,

    override val filteringUiInfo: FinanceFilteringUiInfo = FinanceFilteringUiInfo(),

    val uiEvent: FinanceUiEvent? = null,
): PagerUiState {

    val hasAppliedFilters: Boolean =
        expensesUiState.filteringUiInfo.hasAppliedFilters ||
        investmentsUiState.filteringUiInfo.hasAppliedFilters ||
        tradesUiState.filteringUiInfo.hasAppliedFilters ||
        savingsUiState.filteringUiInfo.hasAppliedFilters ||
        budgetsUiState.filteringUiInfo.hasAppliedFilters ||
        watchlistAssetsUiState.filteringUiInfo.hasAppliedFilters

    fun withTopbarActions(page: Page): Boolean {
        val uiState = when (page) {
            FinancePage.Expenses -> expensesUiState
            FinancePage.Investments -> investmentsUiState
            FinancePage.Trades -> tradesUiState
            FinancePage.Savings -> savingsUiState
            FinancePage.Budgets -> budgetsUiState
            FinancePage.Watchlist -> watchlistAssetsUiState
            else -> null
        }

        return uiState?.let { !it.isEmpty || it.filteringUiInfo.hasAppliedFilters } ?: false
    }

}