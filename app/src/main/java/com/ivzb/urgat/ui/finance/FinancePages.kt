package com.ivzb.urgat.ui.finance

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.budget.GroupedBudget
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.trade.Trade
import com.ivzb.urgat.data.watchlist.WatchlistAsset
import com.ivzb.urgat.ui.budgets.BudgetsUiState
import com.ivzb.urgat.ui.budgets.CategoriesPage
import com.ivzb.urgat.ui.components.EmptyState
import com.ivzb.urgat.ui.components.ItemHeaderLarge
import com.ivzb.urgat.ui.components.ItemHeaderWithMore
import com.ivzb.urgat.ui.components.uiState.PagerUiState
import com.ivzb.urgat.ui.expenses.ExpensesUiState
import com.ivzb.urgat.ui.investments.InvestmentsUiState
import com.ivzb.urgat.ui.savings.SavingsUiState
import com.ivzb.urgat.ui.trades.TradesUiState
import com.ivzb.urgat.ui.watchlist.WatchlistAssetsUiState

@Composable
private fun FinancePage(
    uiState: PagerUiState,
    onEmptyStateButtonClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    when {
        uiState.isEmpty -> {
            EmptyState(uiState.filteringUiInfo, onEmptyStateButtonClick)
        }

        else -> {
            content()
        }
    }
}

@Composable
internal fun ExpensesPage(
    uiState: ExpensesUiState,

    onMoreExpenses: () -> Unit,
    onAddExpense: () -> Unit,
    onExpenseClick: (Expense) -> Unit,
    onResetFilters: () -> Unit,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    FinancePage(
        uiState = uiState,
        onEmptyStateButtonClick = if (uiState.filteringUiInfo.hasAppliedFilters) onResetFilters else onAddExpense,

        content = {
            com.ivzb.urgat.ui.expenses.TransactionsPage(
                expenses = uiState.expenses,
                onExpenseClick = onExpenseClick,

                header = {
                    ItemHeaderWithMore(
                        title = stringResource(uiState.filteringUiInfo.currentFilteringLabel),
                        onMoreClick = onMoreExpenses,
                    )
                },

                listState = listState,
                onListStateChanged = onListStateChanged,
            )
        }
    )
}

@Composable
internal fun InvestmentsPage(
    uiState: InvestmentsUiState,

    onMoreInvestments: () -> Unit,
    onAddInvestment: () -> Unit,
    onInvestmentClick: (Investment) -> Unit,
    onRefreshInvestments: () -> Unit,
    onResetFilters: () -> Unit,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    FinancePage(
        uiState = uiState,
        onEmptyStateButtonClick = if (uiState.filteringUiInfo.hasAppliedFilters) onResetFilters else onAddInvestment,

        content = {
            val state = rememberPullToRefreshState()

            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = onRefreshInvestments,
                modifier = Modifier,
                state = state,
                indicator = {
                    Indicator(
                        modifier = Modifier.align(Alignment.TopCenter),
                        isRefreshing = uiState.isRefreshing,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        state = state
                    )
                },
            ) {

                com.ivzb.urgat.ui.investments.TransactionsPage(
                    investments = uiState.investments,
                    onInvestmentClick = onInvestmentClick,

                    header = {
                        ItemHeaderWithMore(
                            title = stringResource(uiState.filteringUiInfo.currentFilteringLabel),
                            onMoreClick = onMoreInvestments,
                        )
                    },

                    listState = listState,
                    onListStateChanged = onListStateChanged,
                )
            }
        }
    )
}

@Composable
internal fun TradesPage(
    uiState: TradesUiState,

    onMoreTrades: () -> Unit,
    onAddTrade: () -> Unit,
    onTradeClick: (Trade) -> Unit,
    onRefreshTrades: () -> Unit,
    onResetFilters: () -> Unit,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    FinancePage(
        uiState = uiState,
        onEmptyStateButtonClick = if (uiState.filteringUiInfo.hasAppliedFilters) onResetFilters else onAddTrade,

        content = {
            val state = rememberPullToRefreshState()

            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = onRefreshTrades,
                modifier = Modifier,
                state = state,
                indicator = {
                    Indicator(
                        modifier = Modifier.align(Alignment.TopCenter),
                        isRefreshing = uiState.isRefreshing,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        state = state
                    )
                },
            ) {

                com.ivzb.urgat.ui.trades.TransactionsPage(
                    trades = uiState.trades,
                    onTradeClick = onTradeClick,

                    header = {
                        ItemHeaderWithMore(
                            title = stringResource(uiState.filteringUiInfo.currentFilteringLabel),
                            onMoreClick = onMoreTrades,
                        )
                    },

                    listState = listState,
                    onListStateChanged = onListStateChanged,
                )
            }
        }
    )
}

@Composable
internal fun SavingsPage(
    uiState: SavingsUiState,

    onMoreSavings: () -> Unit,
    onAddSaving: () -> Unit,
    onSavingClick: (Saving) -> Unit,
    onResetFilters: () -> Unit,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    FinancePage(
        uiState = uiState,
        onEmptyStateButtonClick = if (uiState.filteringUiInfo.hasAppliedFilters) onResetFilters else onAddSaving,
        content = {
            com.ivzb.urgat.ui.savings.TransactionsPage(
                savings = uiState.savings,
                onSavingClick = onSavingClick,

                header = {
                    ItemHeaderWithMore(
                        title = stringResource(uiState.filteringUiInfo.currentFilteringLabel),
                        onMoreClick = onMoreSavings,
                    )
                },

                listState = listState,
                onListStateChanged = onListStateChanged,
            )
        }
    )
}

@Composable
internal fun BudgetsPage(
    uiState: BudgetsUiState,

    onMoreBudgets: () -> Unit,
    onAddBudget: () -> Unit,
    onBudgetClick: (Budget) -> Unit,
    onResetFilters: () -> Unit,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    FinancePage(
        uiState = uiState,
        onEmptyStateButtonClick = if (uiState.filteringUiInfo.hasAppliedFilters) onResetFilters else onAddBudget,
        content = {
            com.ivzb.urgat.ui.budgets.TransactionsPage(
                budgets = uiState.budgets,
                onBudgetClick = onBudgetClick,

                header = {
                    ItemHeaderWithMore(
                        title = stringResource(uiState.filteringUiInfo.currentFilteringLabel),
                        onMoreClick = onMoreBudgets,
                    )
                },

                listState = listState,
                onListStateChanged = onListStateChanged,
            )
        }
    )
}

@Composable
internal fun ReportPage(
    reportBudget: GroupedBudget?,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    CategoriesPage(
        budget = reportBudget,
        group = null,

        listState = listState,
        onListStateChanged = onListStateChanged,
    )
}

@Composable
internal fun WatchlistPage(
    uiState: WatchlistAssetsUiState,

    onAddWatchlistAsset: () -> Unit,
    onWatchlistAssetClick: (WatchlistAsset) -> Unit,
    onRefreshWatchlist: () -> Unit,
    onResetFilters: () -> Unit,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    FinancePage(
        uiState = uiState,
        onEmptyStateButtonClick = if (uiState.filteringUiInfo.hasAppliedFilters) onResetFilters else onAddWatchlistAsset,
        content = {
            val state = rememberPullToRefreshState()

            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = onRefreshWatchlist,
                modifier = Modifier,
                state = state,
                indicator = {
                    Indicator(
                        modifier = Modifier.align(Alignment.TopCenter),
                        isRefreshing = uiState.isRefreshing,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        state = state
                    )
                },
            ) {

                com.ivzb.urgat.ui.watchlist.TransactionsPage(
                    watchlistAssets = uiState.watchlistAssets,
                    onWatchlistAssetClick = onWatchlistAssetClick,

                    header = {
                        ItemHeaderLarge(
                            text = stringResource(uiState.filteringUiInfo.currentFilteringLabel),
                        )
                    },

                    listState = listState,
                    onListStateChanged = onListStateChanged,
                )
            }
        }
    )
}
