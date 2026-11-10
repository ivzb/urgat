package com.ivzb.urgat.ui.trades

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.data.trade.Trade
import com.ivzb.urgat.data.trade.TradeTotal.Companion.sumByCurrency
import com.ivzb.urgat.domain.trades.GroupedTrades
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.ItemHeaderSmall
import com.ivzb.urgat.ui.components.ListItem
import com.ivzb.urgat.ui.components.MoreMenu
import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.components.SortMenu
import com.ivzb.urgat.ui.components.screens.PagerScreen
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.util.annotateTradesTotals
import com.ivzb.urgat.util.format
import com.ivzb.urgat.util.formatCurrency
import com.ivzb.urgat.util.isFirst
import com.ivzb.urgat.util.paddingIf
import com.ivzb.urgat.util.toggle

@Composable
fun TradesScreen(
    viewModel: TradesViewModel,

    onTradeClick: (Trade) -> Unit,
    onBack: () -> Unit,

    modifier: Modifier = Modifier,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TradesScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onTradeClick = onTradeClick,
        onBack = onBack,
        modifier = modifier
    )
}

@VisibleForTesting
@Composable
fun TradesScreen(
    uiState: TradesUiState,
    onIntent: (TradesIntent) -> Unit,

    onTradeClick: (Trade) -> Unit,

    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val expandedMonthlyIds = rememberSaveable { mutableStateSetOf<String>() }
    val expandedYearlyIds = rememberSaveable { mutableStateSetOf<String>() }
    val expandedAllTimeIds = rememberSaveable { mutableStateSetOf<String>() }

    PagerScreen(
        uiState = uiState,

        route = TradesRoute,
        searchQuery = uiState.filteringUiInfo.searchQuery,
        onSearchQueryChanged = { query -> onIntent(TradesIntent.SearchQueryChanged(query)) },
        onBack = onBack,

        topAppBarActionsContent = {
            TradesTopAppBarActions(
                uiState = uiState,
                onIntent = onIntent,
            )
        },

        content = { page, listState, onListStateChanged ->
            TradesPageContent(
                page = page,
                uiState = uiState,
                onIntent = onIntent,
                onTradeClick = onTradeClick,
                expandedMonthlyIds = expandedMonthlyIds,
                expandedYearlyIds = expandedYearlyIds,
                expandedAllTimeIds = expandedAllTimeIds,
                listState = listState,
                onListStateChanged = onListStateChanged,
            )
        },

        modifier = modifier,
    )
}

@Composable
private fun TradesTopAppBarActions(
    uiState: TradesUiState,
    onIntent: (TradesIntent) -> Unit,
) {
    SortMenu(
        items = uiState.filteringUiInfo.sortMenuItems,
        showBadge = uiState.filteringUiInfo.showSortBadge,
        onFilter = { onIntent(TradesIntent.SetSorting(it)) },
    )

    MoreMenu(
        listOfNotNull(
            (R.string.reset_filters to { onIntent(TradesIntent.ResetFilters) }).takeIf { uiState.filteringUiInfo.hasAppliedFilters },
        )
    )
}

@Composable
private fun TradesPageContent(
    page: Page,
    uiState: TradesUiState,
    onIntent: (TradesIntent) -> Unit,
    onTradeClick: (Trade) -> Unit,
    expandedMonthlyIds: MutableSet<String>,
    expandedYearlyIds: MutableSet<String>,
    expandedAllTimeIds: MutableSet<String>,
    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {
    when (page) {
        TradesPage.Monthly -> GroupedCategoriesPage(
            categories = uiState.categoriesMonthly,
            filteringUiInfo = uiState.filteringUiInfo,
            expandedCategoryIds = expandedMonthlyIds,
            onCategoryClick = { categoryId -> expandedMonthlyIds.toggle(categoryId) },
            onTradeClick = onTradeClick,
            onResetFilters = { onIntent(TradesIntent.ResetFilters) },
            onListStateChanged = onListStateChanged,
        )

        TradesPage.Yearly -> GroupedCategoriesPage(
            categories = uiState.categoriesYearly,
            filteringUiInfo = uiState.filteringUiInfo,
            expandedCategoryIds = expandedYearlyIds,
            onCategoryClick = { categoryId -> expandedYearlyIds.toggle(categoryId) },
            onTradeClick = onTradeClick,
            onResetFilters = { onIntent(TradesIntent.ResetFilters) },
            onListStateChanged = onListStateChanged,
        )

        TradesPage.AllTime -> CategoriesPage(
            group = stringResource(R.string.all_time),
            total = annotateTradesTotals(
                uiState.categoriesAllTime
                    .flatMap { it.totals }
                    .sumByCurrency()
            ),
            categories = uiState.categoriesAllTime,
            expandedCategoryIds = expandedAllTimeIds,

            filteringUiInfo = uiState.filteringUiInfo,
            onResetFilters = { onIntent(TradesIntent.ResetFilters) },

            listState = listState,
            onCategoryClick = { categoryId -> expandedAllTimeIds.toggle(categoryId) },
            onTradeClick = onTradeClick,
            onListStateChanged = onListStateChanged,
        )
    }
}

@Composable
fun TransactionsPage(
    trades: List<GroupedTrades>,
    onTradeClick: (Trade) -> Unit,

    header: (@Composable () -> Unit)? = null,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    LaunchedEffect(Unit) {
        onListStateChanged(listState)
    }

    LazyColumn(
        contentPadding = PaddingValues(
            horizontal = dimensionResource(R.dimen.content_padding_horizontal),
            vertical = dimensionResource(R.dimen.content_padding_vertical),
        ),
        state = listState,
        modifier = Modifier
            .fillMaxSize(),
    ) {

        item {
            header?.invoke()
        }

        trades.forEachIndexed { index, (group, total, trades) ->
            item {
                ItemHeaderSmall(
                    text = group,
                    total = annotateTradesTotals(total),
                    modifier = Modifier
                        .paddingIf(
                            index.isFirst(),
                            bottom = dimensionResource(R.dimen.vertical_margin)
                        )
                        .paddingIf(
                            !index.isFirst(),
                            vertical = dimensionResource(R.dimen.vertical_margin)
                        )
                        .padding(horizontal = dimensionResource(R.dimen.list_item_padding))
                )
            }

            items(
                items = trades,
                key = { trade -> trade.id },
            ) { trade ->

                TradeItem(
                    trade = trade,
                    onTradeClick = onTradeClick
                )
            }
        }
    }
}

@Composable
fun TradeItem(
    trade: Trade,
    onTradeClick: (Trade) -> Unit
) {

    ListItem(
        textLeft = trade.name,

        textRight = "${trade.amount.format()} ${trade.currency.formatCurrency()}",

        onClick = { onTradeClick(trade) }
    )
}

@AllDevicesPreview
@Composable
private fun TradesScreenPreview(initialPage: Int = 0) {
    UrgatTheme {
        TradesScreen(
            uiState = TradesUiState(
                trades = SampleData.SAMPLE_TRADES_GROUPED,
                categoriesMonthly = SampleData.SAMPLE_TRADES_CATEGORIES_MONTHLY,
                categoriesYearly = SampleData.SAMPLE_TRADES_CATEGORIES_YEARLY,
                categoriesAllTime = SampleData.SAMPLE_TRADES_CATEGORIES_ALL_TIME,
                filteringUiInfo = TradesFilteringUiInfo(),
                isLoading = false,
                hasError = false,
                initialPage = initialPage,
            ),
            onIntent = {},
            onTradeClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun TradesScreenMonthlyPreview() {
    TradesScreenPreview(initialPage = 0)
}

@AllDevicesPreview
@Composable
private fun TradesScreenYearlyPreview() {
    TradesScreenPreview(initialPage = 1)
}

@AllDevicesPreview
@Composable
private fun TradesScreenAllTimePreview() {
    TradesScreenPreview(initialPage = 2)
}

@AllDevicesPreview
@Composable
private fun TradesScreenEmptyPreview() {
    UrgatTheme {
        TradesScreen(
            uiState = TradesUiState(
                trades = listOf(),
                filteringUiInfo = TradesFilteringUiInfo(),
                isLoading = false,
                hasError = false,
            ),
            onIntent = {},
            onTradeClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun TradesScreenLoadingPreview() {
    UrgatTheme {
        TradesScreen(
            uiState = TradesUiState(
                isLoading = true,
            ),
            onIntent = {},
            onTradeClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun TradesScreenErrorPreview() {
    UrgatTheme {
        TradesScreen(
            uiState = TradesUiState(
                hasError = true,
            ),
            onIntent = {},
            onTradeClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}
