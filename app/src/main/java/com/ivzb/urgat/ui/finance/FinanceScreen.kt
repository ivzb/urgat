package com.ivzb.urgat.ui.finance

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.budget.GroupedBudget
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.fire.FiRe
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.data.trade.Trade
import com.ivzb.urgat.data.watchlist.WatchlistAsset
import com.ivzb.urgat.navigation.FakeNavigator
import com.ivzb.urgat.navigation.Navigator
import com.ivzb.urgat.ui.budgets.BudgetsFilteringUiInfo
import com.ivzb.urgat.ui.budgets.BudgetsUiState
import com.ivzb.urgat.ui.budgets.CategoriesPage
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.EmptyState
import com.ivzb.urgat.ui.components.FilterMenu
import com.ivzb.urgat.ui.components.ItemHeaderWithMore
import com.ivzb.urgat.ui.components.MoreMenu
import com.ivzb.urgat.ui.components.ScreenTitleWithInfo
import com.ivzb.urgat.ui.components.SortMenu
import com.ivzb.urgat.ui.components.screens.NavigationPagerScreen
import com.ivzb.urgat.ui.components.uiState.PagerUiState
import com.ivzb.urgat.ui.expenses.ExpensesUiState
import com.ivzb.urgat.ui.fire.FireDateRangeType
import com.ivzb.urgat.ui.investments.InvestmentsUiState
import com.ivzb.urgat.ui.savings.SavingsUiState
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.ui.trades.TradesUiState
import com.ivzb.urgat.ui.watchlist.WatchlistAssetsUiState
import com.ivzb.urgat.util.format
import com.ivzb.urgat.util.toLocalDate

private const val EMPTY_FIRE_CALCULATION = "∞"

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
    onMoreWatchlistAssets: () -> Unit,
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

        onMoreWatchlistAssets = onMoreWatchlistAssets,
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

    onMoreWatchlistAssets: () -> Unit,
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
            if (uiState.withTopbarActions(page)) {
                when (page) {
                    FinancePage.Expenses -> {
                        FilterMenu(
                            items = uiState.expensesUiState.filteringUiInfo.filterMenuItems,
                            showBadge = uiState.expensesUiState.filteringUiInfo.showFilterBadge,
                            onFilter = { onIntent(FinanceIntent.SetExpensesFiltering(it)) },
                        )

                        SortMenu(
                            items = uiState.expensesUiState.filteringUiInfo.sortMenuItems,
                            showBadge = uiState.expensesUiState.filteringUiInfo.showSortBadge,
                            onFilter = { onIntent(FinanceIntent.SetExpensesSorting(it)) },
                        )
                    }

                    FinancePage.Investments -> {
                        FilterMenu(
                            items = uiState.investmentsUiState.filteringUiInfo.filterMenuItems,
                            showBadge = uiState.investmentsUiState.filteringUiInfo.showFilterBadge,
                            onFilter = { onIntent(FinanceIntent.SetInvestmentsFiltering(it)) },
                        )

                        SortMenu(
                            items = uiState.investmentsUiState.filteringUiInfo.sortMenuItems,
                            showBadge = uiState.investmentsUiState.filteringUiInfo.showSortBadge,
                            onFilter = { onIntent(FinanceIntent.SetInvestmentsSorting(it)) },
                        )
                    }

                    FinancePage.Trades -> {
                        FilterMenu(
                            items = uiState.tradesUiState.filteringUiInfo.filterMenuItems,
                            showBadge = uiState.tradesUiState.filteringUiInfo.showFilterBadge,
                            onFilter = { onIntent(FinanceIntent.SetTradesFiltering(it)) },
                        )

                        SortMenu(
                            items = uiState.tradesUiState.filteringUiInfo.sortMenuItems,
                            showBadge = uiState.tradesUiState.filteringUiInfo.showSortBadge,
                            onFilter = { onIntent(FinanceIntent.SetTradesSorting(it)) },
                        )
                    }

                    FinancePage.Savings -> {
                        SortMenu(
                            items = uiState.savingsUiState.filteringUiInfo.sortMenuItems,
                            showBadge = uiState.savingsUiState.filteringUiInfo.showSortBadge,
                            onFilter = { onIntent(FinanceIntent.SetSavingsSorting(it)) },
                        )
                    }

                    FinancePage.Budgets -> {
                        FilterMenu(
                            items = uiState.budgetsUiState.filteringUiInfo.filterMenuItems,
                            showBadge = uiState.budgetsUiState.filteringUiInfo.showFilterBadge,
                            onFilter = { onIntent(FinanceIntent.SetBudgetsFiltering(it)) },
                        )

                        SortMenu(
                            items = uiState.budgetsUiState.filteringUiInfo.sortMenuItems,
                            showBadge = uiState.budgetsUiState.filteringUiInfo.showSortBadge,
                            onFilter = { onIntent(FinanceIntent.SetBudgetsSorting(it)) },
                        )
                    }

                    FinancePage.Watchlist -> {
                        SortMenu(
                            items = uiState.watchlistAssetsUiState.filteringUiInfo.sortMenuItems,
                            showBadge = uiState.watchlistAssetsUiState.filteringUiInfo.showSortBadge,
                            onFilter = { onIntent(FinanceIntent.SetWatchlistAssetsSorting(it)) },
                        )
                    }
                }

                MoreMenu(
                    listOfNotNull(
                        (R.string.reset_filters to { onIntent(FinanceIntent.ResetFilters) }).takeIf { uiState.hasAppliedFilters },
                    )
                )
            }
        },

        content = { page, listState, onListStateChanged ->
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

                    onMoreWatchlistAssets = onMoreWatchlistAssets,
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
        },

        modifier = modifier
    )
}

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
private fun ExpensesPage(
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
private fun InvestmentsPage(
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
private fun TradesPage(
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
private fun SavingsPage(
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
private fun BudgetsPage(
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
private fun ReportPage(
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
private fun WatchlistPage(
    uiState: WatchlistAssetsUiState,

    onMoreWatchlistAssets: () -> Unit,
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
                        ItemHeaderWithMore(
                            title = stringResource(uiState.filteringUiInfo.currentFilteringLabel),
                            onMoreClick = onMoreWatchlistAssets,
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
private fun FiRePage(
    fire: FiRe?,
    fireDateRangeType: FireDateRangeType,
    fireCustomStart: Long,
    fireCustomEnd: Long,
    onDateRangeTypeChange: (FireDateRangeType) -> Unit,
    onCustomDateRangeChange: (Long, Long) -> Unit,
    onWithdrawalRateChange: (String) -> Unit,
    onReturnOfInvestmentChange: (String) -> Unit,
    onTaxRateChange: (String) -> Unit,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    LaunchedEffect(Unit) {
        onListStateChanged(listState)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = dimensionResource(R.dimen.content_padding_horizontal),
                vertical = dimensionResource(R.dimen.content_padding_vertical),
            )
    ) {

        Text(
            text = stringResource(R.string.fire_date_range),
        )

        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.fire_margin)))

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth(),
        ) {
            FireDateRangeType.entries.forEachIndexed { index, type ->
                SegmentedButton(
                    selected = fireDateRangeType == type,
                    onClick = { onDateRangeTypeChange(type) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = FireDateRangeType.entries.size,
                    ),
                    icon = {},
                    border = SegmentedButtonDefaults.borderStroke(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    ),
                ) {
                    Text(stringResource(type.labelResId))
                }
            }
        }

        if (fireDateRangeType == FireDateRangeType.CUSTOM) {
            Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.fire_margin)))

            FireCustomDateRangePicker(
                customStart = fireCustomStart,
                customEnd = fireCustomEnd,
                onCustomDateRangeChange = onCustomDateRangeChange,
            )
        }

        if (fire == null) {
            Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.fire_margin)))

            Text(
                text = stringResource(R.string.fire_not_enough_data),
                style = MaterialTheme.typography.bodyLarge,
            )
            return
        }

        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.fire_margin)))

        ScreenTitleWithInfo(
            title = stringResource(R.string.fire_title, fire.selection),
            dialogTitle = stringResource(R.string.how_does_it_work),
            dialogText = StringBuilder().apply {
                appendLine(stringResource(R.string.fire_info))
                appendLine()

                appendLine(
                    stringResource(
                        R.string.fire_accumulated_stash,
                        "${fire.accumulatedStash.format()} ${fire.currency}",
                    )
                )

                fire.stashBreakdown.forEach { item ->
                    appendLine("    ${item.name}: ${item.amount.format()} ${fire.currency}")
                }

                appendLine()

                appendLine(
                    stringResource(
                        R.string.fire_annual_income,
                        "${fire.annualIncome.format()} ${fire.currency}",
                    )
                )
                appendLine(
                    stringResource(
                        R.string.fire_annual_expenses,
                        "${fire.annualExpenses.format()} ${fire.currency}",
                    )
                )
                appendLine(
                    stringResource(
                        R.string.fire_annual_investments,
                        "${fire.annualInvestments.format()} ${fire.currency}",
                    )
                )

                appendLine()

                appendLine(
                    stringResource(
                        R.string.fire_savings_rate,
                        fire.savingsRate?.format() ?: EMPTY_FIRE_CALCULATION
                    )
                )
                appendLine(
                    stringResource(
                        R.string.fire_target,
                        fire.target?.let { target -> "${target.format()} ${fire.currency}" } ?: EMPTY_FIRE_CALCULATION
                    )
                )

                appendLine()

                appendLine(
                    stringResource(
                        R.string.fire_years_until_fire,
                        fire.yearsUntilFiRe?.format() ?: EMPTY_FIRE_CALCULATION
                    )
                )
            }.toString(),
        )

        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.fire_margin)))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.fire_withdrawal_rate),
            )

            Spacer(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.fire_margin)))

            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.fire_return_of_investment),
            )

            Spacer(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.fire_margin)))

            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.fire_tax_rate),
            )
        }

        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.fire_margin)))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            var withdrawalRateValue by remember {
                mutableStateOf(
                    TextFieldValue(
                        fire.withdrawalRate?.toPlainString() ?: ""
                    )
                )
            }
            var returnOfInvestmentValue by remember {
                mutableStateOf(
                    TextFieldValue(
                        fire.returnOfInvestment?.toPlainString() ?: ""
                    )
                )
            }
            var taxRateValue by remember {
                mutableStateOf(
                    TextFieldValue(
                        fire.taxRate?.toPlainString() ?: ""
                    )
                )
            }

            TextField(
                modifier = Modifier.weight(1f),
                value = withdrawalRateValue,
                onValueChange = { onWithdrawalRateChange(it.text) },
                placeholder = {
                    Text(
                        stringResource(
                            R.string.format_default,
                            fire.withdrawalRatePlaceholder.toPlainString()
                        )
                    )
                },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
            )

            Spacer(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.fire_margin)))

            TextField(
                modifier = Modifier.weight(1f),
                value = returnOfInvestmentValue,
                onValueChange = { onReturnOfInvestmentChange(it.text) },
                placeholder = {
                    Text(
                        stringResource(
                            R.string.format_default,
                            fire.returnOfInvestmentPlaceholder.toPlainString()
                        )
                    )
                },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
            )

            Spacer(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.fire_margin)))

            TextField(
                modifier = Modifier.weight(1f),
                value = taxRateValue,
                onValueChange = { onTaxRateChange(it.text) },
                placeholder = {
                    Text(
                        stringResource(
                            R.string.format_default,
                            fire.taxRatePlaceholder.toPlainString()
                        )
                    )
                },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
            )

            LaunchedEffect(fire) {
                val withdrawalRate = fire.withdrawalRate?.toPlainString() ?: ""
                val returnOfInvestment = fire.returnOfInvestment?.toPlainString() ?: ""
                val taxRate = fire.taxRate?.toPlainString() ?: ""

                withdrawalRateValue = TextFieldValue(
                    text = withdrawalRate,
                    selection = TextRange(withdrawalRate.length)
                )

                returnOfInvestmentValue = TextFieldValue(
                    text = returnOfInvestment,
                    selection = TextRange(returnOfInvestment.length)
                )

                taxRateValue = TextFieldValue(
                    text = taxRate,
                    selection = TextRange(taxRate.length)
                )
            }
        }

        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.vertical_margin)))

        Text(
            text = stringResource(
                R.string.fire_years_until_fire,
                fire.yearsUntilFiRe?.format() ?: EMPTY_FIRE_CALCULATION
            ),
            fontWeight = FontWeight.Bold,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FireCustomDateRangePicker(
    customStart: Long,
    customEnd: Long,
    onCustomDateRangeChange: (Long, Long) -> Unit,
) {
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedButton(
            onClick = { showStartPicker = true },
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = if (customStart > 0L) {
                    customStart.toLocalDate().format()
                } else {
                    stringResource(R.string.fire_select_start_date)
                }
            )
        }

        Spacer(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.fire_margin)))

        OutlinedButton(
            onClick = { showEndPicker = true },
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = if (customEnd > 0L) {
                    customEnd.toLocalDate().format()
                } else {
                    stringResource(R.string.fire_select_end_date)
                }
            )
        }
    }

    if (showStartPicker) {
        FireDatePickerDialog(
            title = stringResource(R.string.fire_custom_start_date),
            initialSelectedDateMillis = customStart.takeIf { it > 0L },
            onDateSelected = { selectedDate ->
                selectedDate?.let { start ->
                    onCustomDateRangeChange(start, if (customEnd > 0L) customEnd else start)
                }
            },
            onDismiss = { showStartPicker = false },
        )
    }

    if (showEndPicker) {
        FireDatePickerDialog(
            title = stringResource(R.string.fire_custom_end_date),
            initialSelectedDateMillis = customEnd.takeIf { it > 0L },
            onDateSelected = { selectedDate ->
                selectedDate?.let { end ->
                    onCustomDateRangeChange(if (customStart > 0L) customStart else end, end)
                }
            },
            onDismiss = { showEndPicker = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FireDatePickerDialog(
    title: String,
    initialSelectedDateMillis: Long?,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis,
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismiss()
                },
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    ) {
        DatePicker(
            state = datePickerState,
            title = { Text(text = title, modifier = Modifier.padding(start = dimensionResource(R.dimen.content_padding_horizontal), top = dimensionResource(R.dimen.content_padding_vertical))) },
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

            onMoreWatchlistAssets = {},
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

            onMoreWatchlistAssets = {},
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

            onMoreWatchlistAssets = {},
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

            onMoreWatchlistAssets = {},
            onWatchlistAssetClick = {},

            navigator = FakeNavigator(),
            modifier = Modifier
        )
    }
}