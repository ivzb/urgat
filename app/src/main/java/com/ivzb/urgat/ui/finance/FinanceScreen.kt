package com.ivzb.urgat.ui.finance

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.data.trade.Trade
import com.ivzb.urgat.data.watchlist.WatchlistAsset
import com.ivzb.urgat.navigation.FakeNavigator
import com.ivzb.urgat.navigation.Navigator
import com.ivzb.urgat.ui.budgets.BudgetsFilteringUiInfo
import com.ivzb.urgat.ui.budgets.BudgetsUiState
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.MoreMenu
import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.components.screens.NavigationPagerScreen
import com.ivzb.urgat.ui.expenses.ExpensesUiState
import com.ivzb.urgat.ui.investments.InvestmentsUiState
import com.ivzb.urgat.ui.savings.SavingsUiState
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.ui.trades.TradesUiState
import com.ivzb.urgat.ui.watchlist.WatchlistAssetsUiState

@Composable
fun FinanceScreen(
    viewModel: FinanceViewModel,

    onAddExpense: () -> Unit,
    onMoreExpenses: () -> Unit,
    onExpenseClick: (Expense) -> Unit,

    onAddInvestment: () -> Unit,
    onMoreInvestments: () -> Unit,
    onInvestmentClick: (Investment) -> Unit,

    onAddTrade: () -> Unit,
    onMoreTrades: () -> Unit,
    onTradeClick: (Trade) -> Unit,

    onAddSaving: () -> Unit,
    onMoreSavings: () -> Unit,
    onSavingClick: (Saving) -> Unit,

    onAddBudget: () -> Unit,
    onMoreBudgets: () -> Unit,
    onBudgetClick: (Budget) -> Unit,

    onAddWatchlistAsset: () -> Unit,
    onWatchlistAssetClick: (WatchlistAsset) -> Unit,

    navigator: Navigator,
    modifier: Modifier = Modifier,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FinanceScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,

        onMoreExpenses = onMoreExpenses,
        onExpenseClick = onExpenseClick,

        onMoreInvestments = onMoreInvestments,
        onInvestmentClick = onInvestmentClick,

        onMoreTrades = onMoreTrades,
        onTradeClick = onTradeClick,

        onMoreSavings = onMoreSavings,
        onSavingClick = onSavingClick,

        onMoreBudgets = onMoreBudgets,
        onBudgetClick = onBudgetClick,

        onWatchlistAssetClick = onWatchlistAssetClick,

        navigator = navigator,
        modifier = modifier
    )

    LaunchedEffect(uiState.uiEvent) {
        uiState.uiEvent?.let { event ->
            when (event) {
                is FinanceUiEvent.AddExpense -> onAddExpense()
                is FinanceUiEvent.AddInvestment -> onAddInvestment()
                is FinanceUiEvent.AddTrade -> onAddTrade()
                is FinanceUiEvent.AddSaving -> onAddSaving()
                is FinanceUiEvent.AddBudget -> onAddBudget()
                is FinanceUiEvent.AddWatchlistAsset -> onAddWatchlistAsset()
            }

            viewModel.onIntent(FinanceIntent.EventConsumed)
        }
    }
}

@VisibleForTesting
@Composable
fun FinanceScreen(
    uiState: FinanceUiState,
    onIntent: (FinanceIntent) -> Unit,

    onMoreExpenses: () -> Unit,
    onExpenseClick: (Expense) -> Unit,

    onMoreInvestments: () -> Unit,
    onInvestmentClick: (Investment) -> Unit,

    onMoreTrades: () -> Unit,
    onTradeClick: (Trade) -> Unit,

    onMoreSavings: () -> Unit,
    onSavingClick: (Saving) -> Unit,

    onMoreBudgets: () -> Unit,
    onBudgetClick: (Budget) -> Unit,

    onWatchlistAssetClick: (WatchlistAsset) -> Unit,

    navigator: Navigator,
    modifier: Modifier = Modifier
) {

    var isSearchFocused by remember { mutableStateOf(false) }

    NavigationPagerScreen(
        uiState = uiState,

        route = FinanceRoute,
        searchQuery = uiState.filteringUiInfo.searchQuery,
        onSearchQueryChanged = { query -> onIntent(FinanceIntent.SearchQueryChanged(query)) },
        onFabActionClick = { page -> onIntent(FinanceIntent.FabActionClick(page)) },
        navigator = navigator,

        withSearch = { page -> uiState.withTopbarActions(page) },
        withFab = { page -> page != FinancePage.Report && page != FinancePage.FIRE && !isSearchFocused },
        onSearchFocusChanged = { isSearchFocused = it },
        topAppBarActionsContent = { page ->
            FinanceTopAppBarActions(
                page = page,
                uiState = uiState,
                onIntent = onIntent,
            )
        },

        content = { page, listState, onListStateChanged ->
            FinancePageContent(
                page = page,
                uiState = uiState,
                onIntent = onIntent,

                onMoreExpenses = onMoreExpenses,
                onExpenseClick = onExpenseClick,

                onMoreInvestments = onMoreInvestments,
                onInvestmentClick = onInvestmentClick,

                onMoreTrades = onMoreTrades,
                onTradeClick = onTradeClick,

                onMoreSavings = onMoreSavings,
                onSavingClick = onSavingClick,

                onMoreBudgets = onMoreBudgets,
                onBudgetClick = onBudgetClick,

                onWatchlistAssetClick = onWatchlistAssetClick,

                listState = listState,
                onListStateChanged = onListStateChanged,
            )
        },

        modifier = modifier
    )
}

@Composable
private fun FinanceTopAppBarActions(
    page: Page,
    uiState: FinanceUiState,
    onIntent: (FinanceIntent) -> Unit,
) {
    if (uiState.withTopbarActions(page)) {
        FinanceFilterMenu(page = page, uiState = uiState, onIntent = onIntent)
        FinanceSortMenu(page = page, uiState = uiState, onIntent = onIntent)

        MoreMenu(
            listOfNotNull(
                (R.string.reset_filters to { onIntent(FinanceIntent.ResetFilters) }).takeIf { uiState.hasAppliedFilters },
            )
        )
    }
}

@Composable
private fun FinancePageContent(
    page: Page,
    uiState: FinanceUiState,
    onIntent: (FinanceIntent) -> Unit,

    onMoreExpenses: () -> Unit,
    onExpenseClick: (Expense) -> Unit,

    onMoreInvestments: () -> Unit,
    onInvestmentClick: (Investment) -> Unit,

    onMoreTrades: () -> Unit,
    onTradeClick: (Trade) -> Unit,

    onMoreSavings: () -> Unit,
    onSavingClick: (Saving) -> Unit,

    onMoreBudgets: () -> Unit,
    onBudgetClick: (Budget) -> Unit,

    onWatchlistAssetClick: (WatchlistAsset) -> Unit,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {
    when (page) {
        FinancePage.Expenses, FinancePage.Investments, FinancePage.Trades, FinancePage.Savings -> FinanceTransactionsPageContent(
            page = page,
            uiState = uiState,
            onIntent = onIntent,

            onMoreExpenses = onMoreExpenses,
            onExpenseClick = onExpenseClick,

            onMoreInvestments = onMoreInvestments,
            onInvestmentClick = onInvestmentClick,

            onMoreTrades = onMoreTrades,
            onTradeClick = onTradeClick,

            onMoreSavings = onMoreSavings,
            onSavingClick = onSavingClick,

            listState = listState,
            onListStateChanged = onListStateChanged,
        )

        FinancePage.Budgets, FinancePage.Report, FinancePage.Watchlist, FinancePage.FIRE -> FinancePlanningPageContent(
            page = page,
            uiState = uiState,
            onIntent = onIntent,

            onMoreBudgets = onMoreBudgets,
            onBudgetClick = onBudgetClick,

            onWatchlistAssetClick = onWatchlistAssetClick,

            listState = listState,
            onListStateChanged = onListStateChanged,
        )
    }
}

@Composable
private fun FinanceTransactionsPageContent(
    page: Page,
    uiState: FinanceUiState,
    onIntent: (FinanceIntent) -> Unit,

    onMoreExpenses: () -> Unit,
    onExpenseClick: (Expense) -> Unit,

    onMoreInvestments: () -> Unit,
    onInvestmentClick: (Investment) -> Unit,

    onMoreTrades: () -> Unit,
    onTradeClick: (Trade) -> Unit,

    onMoreSavings: () -> Unit,
    onSavingClick: (Saving) -> Unit,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {
    when (page) {
        FinancePage.Expenses -> ExpensesPage(
            uiState = uiState.expensesUiState,

            onMoreExpenses = onMoreExpenses,
            onAddExpense = { onIntent(FinanceIntent.FabActionClick(page)) },
            onExpenseClick = onExpenseClick,
            onResetFilters = { onIntent(FinanceIntent.ResetFilters) },

            listState = listState,
            onListStateChanged = onListStateChanged,
        )

        FinancePage.Investments -> InvestmentsPage(
            uiState = uiState.investmentsUiState,

            onMoreInvestments = onMoreInvestments,
            onAddInvestment = { onIntent(FinanceIntent.FabActionClick(page)) },
            onInvestmentClick = onInvestmentClick,
            onRefreshInvestments = { onIntent(FinanceIntent.RefreshInvestments) },
            onResetFilters = { onIntent(FinanceIntent.ResetFilters) },

            listState = listState,
            onListStateChanged = onListStateChanged,
        )

        FinancePage.Trades -> TradesPage(
            uiState = uiState.tradesUiState,

            onMoreTrades = onMoreTrades,
            onAddTrade = { onIntent(FinanceIntent.FabActionClick(page)) },
            onTradeClick = onTradeClick,
            onRefreshTrades = { onIntent(FinanceIntent.RefreshTrades) },
            onResetFilters = { onIntent(FinanceIntent.ResetFilters) },

            listState = listState,
            onListStateChanged = onListStateChanged,
        )

        FinancePage.Savings -> SavingsPage(
            uiState = uiState.savingsUiState,

            onMoreSavings = onMoreSavings,
            onAddSaving = { onIntent(FinanceIntent.FabActionClick(page)) },
            onSavingClick = onSavingClick,
            onResetFilters = { onIntent(FinanceIntent.ResetFilters) },

            listState = listState,
            onListStateChanged = onListStateChanged,
        )
    }
}

@Composable
private fun FinancePlanningPageContent(
    page: Page,
    uiState: FinanceUiState,
    onIntent: (FinanceIntent) -> Unit,

    onMoreBudgets: () -> Unit,
    onBudgetClick: (Budget) -> Unit,

    onWatchlistAssetClick: (WatchlistAsset) -> Unit,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {
    when (page) {
        FinancePage.Budgets -> BudgetsPage(
            uiState = uiState.budgetsUiState,

            onMoreBudgets = onMoreBudgets,
            onAddBudget = { onIntent(FinanceIntent.FabActionClick(page)) },
            onBudgetClick = onBudgetClick,
            onResetFilters = { onIntent(FinanceIntent.ResetFilters) },

            listState = listState,
            onListStateChanged = onListStateChanged,
        )

        FinancePage.Report -> ReportPage(
            reportBudget = uiState.reportBudget,

            listState = listState,
            onListStateChanged = onListStateChanged,
        )

        FinancePage.Watchlist -> WatchlistPage(
            uiState = uiState.watchlistAssetsUiState,

            onAddWatchlistAsset = { onIntent(FinanceIntent.FabActionClick(page)) },
            onWatchlistAssetClick = onWatchlistAssetClick,
            onRefreshWatchlist = { onIntent(FinanceIntent.RefreshWatchlist) },
            onResetFilters = { onIntent(FinanceIntent.ResetFilters) },

            listState = listState,
            onListStateChanged = onListStateChanged,
        )

        FinancePage.FIRE -> FiRePage(
            fire = uiState.fire,
            fireDateRangeType = uiState.fireDateRangeType,
            fireCustomStart = uiState.fireCustomStart,
            fireCustomEnd = uiState.fireCustomEnd,
            onDateRangeTypeChange = { onIntent(FinanceIntent.UpdateFireDateRangeType(it)) },
            onCustomDateRangeChange = { start, end -> onIntent(FinanceIntent.UpdateFireCustomDateRange(start, end)) },
            onWithdrawalRateChange = { onIntent(FinanceIntent.UpdateFireWithdrawalRate(it)) },
            onReturnOfInvestmentChange = { onIntent(FinanceIntent.UpdateFireReturnOfInvestment(it)) },
            onTaxRateChange = { onIntent(FinanceIntent.UpdateFireTaxRate(it)) },

            listState = listState,
            onListStateChanged = onListStateChanged,
        )
    }
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenPreview(
    initialPage: Int = 0,
    expensesInitialPage: Int = 0,
    investmentsInitialPage: Int = 0,
    savingsInitialPage: Int = 0,
    budgetsInitialPage: Int = 0,
) {
    UrgatTheme {
        FinanceScreen(
            uiState = FinanceUiState(
                expensesUiState = ExpensesUiState(
                    expenses = SampleData.SAMPLE_EXPENSES_GROUPED,
                    categoriesMonthly = SampleData.SAMPLE_EXPENSES_CATEGORIES_MONTHLY,
                    categoriesYearly = SampleData.SAMPLE_EXPENSES_CATEGORIES_YEARLY,
                    categoriesAllTime = SampleData.SAMPLE_EXPENSES_CATEGORIES_ALL_TIME,
                    initialPage = expensesInitialPage,
                ),
                investmentsUiState = InvestmentsUiState(
                    investments = SampleData.SAMPLE_INVESTMENTS_GROUPED,
                    categoriesYearly = SampleData.SAMPLE_INVESTMENTS_CATEGORIES_YEARLY,
                    assets = SampleData.SAMPLE_INVESTMENTS_ASSETS,
                    categories = SampleData.SAMPLE_INVESTMENTS_CATEGORIES,
                    netWorth = SampleData.SAMPLE_INVESTMENTS_NET_WORTH,
                    initialPage = investmentsInitialPage,
                ),
                savingsUiState = SavingsUiState(
                    savings = SampleData.SAMPLE_SAVINGS_GROUPED,
                    categoriesMonthly = SampleData.SAMPLE_SAVINGS_CATEGORIES_MONTHLY,
                    categoriesYearly = SampleData.SAMPLE_SAVINGS_CATEGORIES_YEARLY,
                    categoriesAllTime = SampleData.SAMPLE_SAVINGS_CATEGORIES_ALL_TIME,
                    initialPage = savingsInitialPage,
                ),
                budgetsUiState = BudgetsUiState(
                    budgets = SampleData.SAMPLE_BUDGETS,
                    filteringUiInfo = BudgetsFilteringUiInfo(),
                    initialPage = budgetsInitialPage,
                ),
                tradesUiState = TradesUiState(
                    trades = SampleData.SAMPLE_TRADES_GROUPED,
                ),
                watchlistAssetsUiState = WatchlistAssetsUiState(
                    watchlistAssets = SampleData.SAMPLE_WATCHLIST_ASSETS,
                ),
                reportBudget = SampleData.SAMPLE_REPORT_BUDGET,
                fire = SampleData.SAMPLE_FIRE,
                isLoading = false,
                hasError = false,
                initialPage = initialPage,
                filteringUiInfo = FinanceFilteringUiInfo()
            ),
            onIntent = {},

            onExpenseClick = {},
            onMoreExpenses = {},

            onInvestmentClick = {},
            onMoreInvestments = {},

            onTradeClick = {},
            onMoreTrades = {},

            onSavingClick = {},
            onMoreSavings = {},

            onBudgetClick = {},
            onMoreBudgets = {},

            onWatchlistAssetClick = {},

            navigator = FakeNavigator(),
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenExpensesTransactionsPreview() {
    FinanceScreenPreview(initialPage = 0, expensesInitialPage = 0)
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenExpensesMonthlyPreview() {
    FinanceScreenPreview(initialPage = 0, expensesInitialPage = 1)
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenExpensesYearlyPreview() {
    FinanceScreenPreview(initialPage = 0, expensesInitialPage = 2)
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenExpensesAllTimePreview() {
    FinanceScreenPreview(initialPage = 0, expensesInitialPage = 3)
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenInvestmentsTransactionsPreview() {
    FinanceScreenPreview(initialPage = 1, investmentsInitialPage = 0)
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenInvestmentsYearlyPreview() {
    FinanceScreenPreview(initialPage = 1, investmentsInitialPage = 1)
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenInvestmentsAllTimePreview() {
    FinanceScreenPreview(initialPage = 1, investmentsInitialPage = 2)
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenInvestmentsAssetsPreview() {
    FinanceScreenPreview(initialPage = 1, investmentsInitialPage = 3)
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenTradeTransactionsPreview() {
    FinanceScreenPreview(initialPage = 2, investmentsInitialPage = 0)
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenTradeAllTimePreview() {
    FinanceScreenPreview(initialPage = 2, investmentsInitialPage = 2)
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenSavingsTransactionsPreview() {
    FinanceScreenPreview(initialPage = 3, expensesInitialPage = 0)
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenSavingsMonthlyPreview() {
    FinanceScreenPreview(initialPage = 3, expensesInitialPage = 1)
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenSavingsYearlyPreview() {
    FinanceScreenPreview(initialPage = 3, expensesInitialPage = 2)
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenSavingsAllTimePreview() {
    FinanceScreenPreview(initialPage = 3, expensesInitialPage = 3)
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenBudgetsTransactionsPreview() {
    FinanceScreenPreview(initialPage = 4, expensesInitialPage = 0)
}

@AllDevicesPreview
@Composable
private fun FinanceScreenReportPreview() {
    FinanceScreenPreview(initialPage = 5)
}

@AllDevicesPreview
@Composable
private fun FinanceScreenWatchlistPreview() {
    FinanceScreenPreview(initialPage = 6)
}

@AllDevicesPreview
@Composable
private fun FinanceScreenFiRePreview() {
    FinanceScreenPreview(initialPage = 7)
}

@AllDevicesPreview
@Composable
fun FinanceScreenEmptyPreview() {
    UrgatTheme {
        FinanceScreen(
            uiState = FinanceUiState(
                expensesUiState = ExpensesUiState(),
                investmentsUiState = InvestmentsUiState(),
                savingsUiState = SavingsUiState(),
                isLoading = false,
                hasError = false,
                filteringUiInfo = FinanceFilteringUiInfo()
            ),
            onIntent = {},

            onExpenseClick = {},
            onMoreExpenses = {},

            onInvestmentClick = {},
            onMoreInvestments = {},

            onTradeClick = {},
            onMoreTrades = {},

            onSavingClick = {},
            onMoreSavings = {},

            onBudgetClick = {},
            onMoreBudgets = {},

            onWatchlistAssetClick = {},

            navigator = FakeNavigator(),
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
fun FinanceScreenLoadingPreview() {
    UrgatTheme {
        FinanceScreen(
            uiState = FinanceUiState(
                expensesUiState = ExpensesUiState(),
                investmentsUiState = InvestmentsUiState(),
                savingsUiState = SavingsUiState(),
                isLoading = true,
                hasError = false,
                filteringUiInfo = FinanceFilteringUiInfo()
            ),
            onIntent = {},

            onExpenseClick = {},
            onMoreExpenses = {},

            onInvestmentClick = {},
            onMoreInvestments = {},

            onTradeClick = {},
            onMoreTrades = {},

            onSavingClick = {},
            onMoreSavings = {},

            onBudgetClick = {},
            onMoreBudgets = {},

            onWatchlistAssetClick = {},

            navigator = FakeNavigator(),
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
fun FinanceScreenErrorPreview() {
    UrgatTheme {
        FinanceScreen(
            uiState = FinanceUiState(
                expensesUiState = ExpensesUiState(),
                investmentsUiState = InvestmentsUiState(),
                savingsUiState = SavingsUiState(),
                isLoading = false,
                hasError = true,
                filteringUiInfo = FinanceFilteringUiInfo()
            ),
            onIntent = {},

            onExpenseClick = {},
            onMoreExpenses = {},

            onInvestmentClick = {},
            onMoreInvestments = {},

            onTradeClick = {},
            onMoreTrades = {},

            onSavingClick = {},
            onMoreSavings = {},

            onBudgetClick = {},
            onMoreBudgets = {},

            onWatchlistAssetClick = {},

            navigator = FakeNavigator(),
            modifier = Modifier
        )
    }
}
