package com.ivzb.urgat.ui.trades

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.data.trade.Trade
import com.ivzb.urgat.data.trade.TradeCategory
import com.ivzb.urgat.data.trade.TradeTotal.Companion.sumByCurrency
import com.ivzb.urgat.domain.trades.GroupedTradeCategories
import com.ivzb.urgat.domain.trades.GroupedTrades
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.EmptyState
import com.ivzb.urgat.ui.components.FilteringUiInfo
import com.ivzb.urgat.ui.components.ItemHeaderSmall
import com.ivzb.urgat.ui.components.ListItem
import com.ivzb.urgat.ui.components.MoreMenu
import com.ivzb.urgat.ui.components.SortMenu
import com.ivzb.urgat.ui.components.charts.PieChart
import com.ivzb.urgat.ui.components.charts.PieChartItem
import com.ivzb.urgat.ui.components.screens.PagerScreen
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.util.annotateChart
import com.ivzb.urgat.util.annotateTradesTotals
import com.ivzb.urgat.util.format
import com.ivzb.urgat.util.formatCurrency
import com.ivzb.urgat.util.isFirst
import com.ivzb.urgat.util.isLast
import com.ivzb.urgat.util.paddingIf
import com.ivzb.urgat.util.toggle
import kotlinx.coroutines.launch

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
        },

        content = { page, listState, onListStateChanged ->
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
        },

        modifier = modifier,
    )
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

@Composable
private fun GroupedCategoriesPage(
    categories: List<GroupedTradeCategories>,
    filteringUiInfo: FilteringUiInfo,
    expandedCategoryIds: Set<String>,
    onCategoryClick: (String) -> Unit,
    onTradeClick: (Trade) -> Unit,
    onResetFilters: () -> Unit,
    onListStateChanged: (LazyListState) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState { categories.size }
    val nestedListStates = remember(categories.size) {
        List(categories.size) { LazyListState() }
    }

    if (categories.isEmpty()) {
        EmptyState(
            filteringUiInfo = filteringUiInfo,
            onButtonClick = { onResetFilters() },
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        PrimaryScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 0.dp,
        ) {
            categories.forEachIndexed { index, category ->
                Tab(
                    text = {
                        Text(
                            text = category.group,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = true,
            modifier = Modifier
                .fillMaxSize(),
        ) { pageIndex ->

            val category = categories[pageIndex]

            CategoriesPage(
                group = category.group,
                total = annotateTradesTotals(category.total),

                categories = category.items,
                expandedCategoryIds = expandedCategoryIds,

                filteringUiInfo = filteringUiInfo,
                onResetFilters = onResetFilters,

                onCategoryClick = onCategoryClick,
                onTradeClick = onTradeClick,

                listState = nestedListStates[pageIndex],
                onListStateChanged = onListStateChanged,
            )
        }
    }
}

@Composable
private fun CategoriesPage(
    group: String,
    total: AnnotatedString?,

    categories: List<TradeCategory>,
    expandedCategoryIds: Set<String>,

    filteringUiInfo: FilteringUiInfo,
    onResetFilters: () -> Unit,

    onCategoryClick: (String) -> Unit,
    onTradeClick: (Trade) -> Unit,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    LaunchedEffect(Unit) {
        onListStateChanged(listState)
    }

    if (categories.isEmpty()) {
        EmptyState(
            filteringUiInfo = filteringUiInfo,
            onButtonClick = { onResetFilters() },
        )
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(
            horizontal = dimensionResource(R.dimen.horizontal_margin),
            vertical = dimensionResource(R.dimen.vertical_margin),
        ),
        state = listState,
        modifier = Modifier
            .fillMaxSize(),
    ) {

        item {
            val data = categories.map {
                PieChartItem(
                    value = it.trades.sumOf { it.amount }.toFloat(),
                    title = it.name,
                    total = annotateTradesTotals(it.totals),
                )
            }
            val defaultText = annotateChart(group, total)

            PieChart(
                data = data,
                defaultText = defaultText,
                textStyle = MaterialTheme.typography.titleLarge,
            )
        }

        item {
            Spacer(
                modifier = Modifier
                    .height(dimensionResource(R.dimen.vertical_margin))
            )
        }

        categories.forEachIndexed { index, category ->
            item(key = category.id) {
                CategoryItem(
                    category = category,
                    colorIndex = index,
                    onClick = { onCategoryClick(category.id) },
                )
            }

            val isExpanded = expandedCategoryIds.contains(category.id)

            if (isExpanded) {
                subTrades(category.trades, onTradeClick)
            }
        }
    }
}

private fun LazyListScope.subTrades(
    trades: List<Trade>,
    onTradeClick: (Trade) -> Unit,
) {
    itemsIndexed(
        trades,
        key = { _, trade -> trade.id },
    ) { index, trade ->

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.horizontal_margin))
                .paddingIf(
                    condition = index.isFirst(),
                    top = dimensionResource(R.dimen.expanded_group_vertical_margin)
                )
                .paddingIf(
                    condition = index.isLast(trades.size),
                    bottom = dimensionResource(R.dimen.expanded_group_vertical_margin)
                ),
            shadowElevation = 2.dp,
        ) {

            TradeItem(
                trade = trade,
                onTradeClick = onTradeClick,
            )
        }
    }
}

@Composable
private fun CategoryItem(
    category: TradeCategory,
    colorIndex: Int,
    onClick: () -> Unit,
) {

    ListItem(
        textLeft = category.name,
        subTextRight = annotateTradesTotals(category.totals),
        colorIndex = colorIndex,
        onClick = onClick,
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

