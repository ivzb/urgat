package com.ivzb.urgat.ui.investments

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
import androidx.compose.runtime.mutableStateListOf
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
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentAsset
import com.ivzb.urgat.data.investment.InvestmentCategory
import com.ivzb.urgat.data.investment.InvestmentNetWorth
import com.ivzb.urgat.data.investment.InvestmentTotal.Companion.formatCostPrice
import com.ivzb.urgat.data.investment.InvestmentTotal.Companion.formatMarketPrice
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.domain.investment.GroupedInvestmentCategories
import com.ivzb.urgat.domain.investment.GroupedInvestments
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.EmptyState
import com.ivzb.urgat.ui.components.FilterMenu
import com.ivzb.urgat.ui.components.FilteringUiInfo
import com.ivzb.urgat.ui.components.ItemHeaderSmall
import com.ivzb.urgat.ui.components.ListItem
import com.ivzb.urgat.ui.components.MoreMenu
import com.ivzb.urgat.ui.components.SortMenu
import com.ivzb.urgat.ui.components.charts.PieChart
import com.ivzb.urgat.ui.components.charts.PieChartItem
import com.ivzb.urgat.ui.components.screens.PagerScreen
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.util.annotateFinancePnL
import com.ivzb.urgat.util.annotateInvestmentsChart
import com.ivzb.urgat.util.format
import com.ivzb.urgat.util.formatCurrency
import com.ivzb.urgat.util.isFirst
import com.ivzb.urgat.util.isLast
import com.ivzb.urgat.util.paddingIf
import com.ivzb.urgat.util.toggle
import kotlinx.coroutines.launch

@Composable
fun InvestmentsScreen(
    viewModel: InvestmentsViewModel,

    onInvestmentClick: (Investment) -> Unit,
    onBack: () -> Unit,

    modifier: Modifier = Modifier,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    InvestmentsScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onInvestmentClick = onInvestmentClick,
        onBack = onBack,
        modifier = modifier
    )
}

@VisibleForTesting
@Composable
fun InvestmentsScreen(
    uiState: InvestmentsUiState,
    onIntent: (InvestmentsIntent) -> Unit,

    onInvestmentClick: (Investment) -> Unit,
    onBack: () -> Unit,

    modifier: Modifier = Modifier,
) {
    val expandedYearlyIds = rememberSaveable { mutableStateListOf<String>() }
    val expandedAssetIds = rememberSaveable { mutableStateListOf<String>() }
    val expandedCategoryIds = rememberSaveable { mutableStateListOf<String>() }

    PagerScreen(
        uiState = uiState,

        route = InvestmentsRoute,
        searchQuery = uiState.filteringUiInfo.searchQuery,
        onSearchQueryChanged = { query -> onIntent(InvestmentsIntent.SearchQueryChanged(query)) },
        onBack = onBack,

        topAppBarActionsContent = {
            FilterMenu(
                items = uiState.filteringUiInfo.filterMenuItems,
                showBadge = uiState.filteringUiInfo.showFilterBadge,
                onFilter = { onIntent(InvestmentsIntent.SetFiltering(it)) },
            )

            SortMenu(
                items = uiState.filteringUiInfo.sortMenuItems,
                showBadge = uiState.filteringUiInfo.showSortBadge,
                onFilter = { onIntent(InvestmentsIntent.SetSorting(it)) },
            )

            MoreMenu(
                listOfNotNull(
                    (R.string.reset_filters to { onIntent(InvestmentsIntent.ResetFilters) }).takeIf { uiState.filteringUiInfo.hasAppliedFilters },
                )
            )
        },

        content = { page, listState, onListStateChanged ->
            when (page) {
                InvestmentsPage.Yearly -> GroupedCategoriesPage(
                    categories = uiState.categoriesYearly,
                    filteringUiInfo = uiState.filteringUiInfo,
                    expandedCategoryIds = expandedYearlyIds,
                    onCategoryClick = { categoryId -> expandedYearlyIds.toggle(categoryId) },
                    onInvestmentClick = onInvestmentClick,
                    onResetFilters = { onIntent(InvestmentsIntent.ResetFilters) },
                    onListStateChanged = onListStateChanged,
                )

                InvestmentsPage.AllTime -> CategoriesPage(
                    group = stringResource(R.string.all_time),
                    total = uiState.netWorth?.totals?.formatMarketPrice(),
                    categories = uiState.categories,
                    expandedCategoryIds = expandedCategoryIds,

                    filteringUiInfo = uiState.filteringUiInfo,
                    onResetFilters = { onIntent(InvestmentsIntent.ResetFilters) },

                    onCategoryClick = { categoryId -> expandedCategoryIds.toggle(categoryId) },
                    onInvestmentClick = onInvestmentClick,

                    listState = listState,
                    onListStateChanged = onListStateChanged,
                )

                InvestmentsPage.Assets -> AssetsPage(
                    group = stringResource(R.string.assets),
                    total = uiState.netWorth?.totals?.formatMarketPrice(),

                    assets = uiState.assets,
                    expandedAssetIds = expandedAssetIds,

                    filteringUiInfo = uiState.filteringUiInfo,
                    onResetFilters = { onIntent(InvestmentsIntent.ResetFilters) },

                    onAssetClick = { assetId -> expandedAssetIds.toggle(assetId) },
                    onInvestmentClick = onInvestmentClick,

                    listState = listState,
                    onListStateChanged = onListStateChanged,
                )
            }
        },

        modifier = modifier,
    )
}

@Composable
fun TransactionsPage(
    investments: List<GroupedInvestments>,
    onInvestmentClick: (Investment) -> Unit,

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

        investments.forEachIndexed { index, (group, total, investments) ->
            item {
                ItemHeaderSmall(
                    text = group,
                    total = total.formatCostPrice(),
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
                items = investments,
                key = { investment -> investment.id },
            ) { investment ->
                InvestmentItem(
                    investment = investment,
                    onInvestmentClick = onInvestmentClick
                )
            }
        }
    }
}

@Composable
private fun GroupedCategoriesPage(
    categories: List<GroupedInvestmentCategories>,
    filteringUiInfo: FilteringUiInfo,
    expandedCategoryIds: List<String>,
    onCategoryClick: (String) -> Unit,
    onInvestmentClick: (Investment) -> Unit,
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
                total = category.total.formatMarketPrice(),
                categories = category.items,
                expandedCategoryIds = expandedCategoryIds,

                filteringUiInfo = filteringUiInfo,
                onResetFilters = onResetFilters,

                onCategoryClick = onCategoryClick,
                onInvestmentClick = onInvestmentClick,

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

    categories: List<InvestmentCategory>,
    expandedCategoryIds: List<String>,

    filteringUiInfo: FilteringUiInfo,
    onResetFilters: () -> Unit,

    onCategoryClick: (String) -> Unit,
    onInvestmentClick: (Investment) -> Unit,

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
            val lastExpandedId = expandedCategoryIds.lastOrNull()
            val expandedCategory = categories.firstOrNull { it.id == lastExpandedId }

            val data: List<PieChartItem>
            val defaultText: AnnotatedString

            if (expandedCategory == null) {
                // all categories pie chart
                data = categories.map {
                    PieChartItem(
                        value = it.investments.sumOf { it.totalMarketPrice }.toFloat(),
                        title = it.name,
                        total = it.totals.formatMarketPrice(),
                    )
                }
                defaultText = annotateInvestmentsChart(group, total)
            } else {
                // expanded category pie chart
                data = expandedCategory.investments
                    .groupBy { it.name }
                    .map { (name, investments) ->
                        val investmentTotal = Investment.total(investments)

                        PieChartItem(
                            value = investments.sumOf { it.totalMarketPrice }.toFloat(),
                            title = name,
                            total = investmentTotal.formatMarketPrice(),
                        )
                    }
                defaultText = annotateInvestmentsChart(expandedCategory.name, expandedCategory.totals.formatMarketPrice())
            }

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
                subInvestments(category.investments, onInvestmentClick)
            }
        }
    }
}

@Composable
private fun AssetsPage(
    group: String,
    total: AnnotatedString?,

    assets: List<InvestmentAsset>,
    expandedAssetIds: List<String>,

    filteringUiInfo: FilteringUiInfo,
    onResetFilters: () -> Unit,

    onAssetClick: (String) -> Unit,
    onInvestmentClick: (Investment) -> Unit,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    LaunchedEffect(Unit) {
        onListStateChanged(listState)
    }

    if (assets.isEmpty()) {
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
            val lastExpandedId = expandedAssetIds.lastOrNull()
            val expandedAsset = assets.firstOrNull { it.id == lastExpandedId }

            val data: List<PieChartItem>
            val defaultText: AnnotatedString

            if (expandedAsset == null) {
                // all assets pie chart
                data = assets.map {
                    PieChartItem(
                        value = it.marketPrice.toFloat(),
                        title = it.name,
                        total = it.totals.formatMarketPrice(),
                    )
                }
                defaultText = annotateInvestmentsChart(group, total)
            } else {
                // expanded asset pie chart
                data = expandedAsset.investments
                    .groupBy { it.name }
                    .map { (name, investments) ->
                        val investmentTotal = Investment.total(investments)

                        PieChartItem(
                            value = investments.sumOf { it.totalMarketPrice }.toFloat(),
                            title = name,
                            total = investmentTotal.formatMarketPrice(),
                        )
                    }
                defaultText = annotateInvestmentsChart(expandedAsset.name, expandedAsset.totals.formatMarketPrice())
            }

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

        assets.forEachIndexed { index, asset ->
            item(key = asset.id) {
                AssetItem(
                    asset = asset,
                    colorIndex = index,
                    onClick = { onAssetClick(asset.id) },
                )
            }

            val isExpanded = expandedAssetIds.contains(asset.id)

            if (isExpanded) {
                subInvestments(asset.investments, onInvestmentClick)
            }
        }
    }
}

private fun LazyListScope.subInvestments(
    investments: List<Investment>,
    onInvestmentClick: (Investment) -> Unit,
) {
    itemsIndexed(
        investments,
        key = { _, investment -> investment.id },
    ) { index, investment ->

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.horizontal_margin))
                .paddingIf(
                    condition = index.isFirst(),
                    top = dimensionResource(R.dimen.expanded_group_vertical_margin)
                )
                .paddingIf(
                    condition = index.isLast(investments.size),
                    bottom = dimensionResource(R.dimen.expanded_group_vertical_margin)
                ),
            shadowElevation = 2.dp,
        ) {

            InvestmentItem(
                investment = investment,
                onInvestmentClick = onInvestmentClick,
            )
        }
    }
}

@Composable
fun InvestmentItem(
    investment: Investment,
    onInvestmentClick: (Investment) -> Unit
) {

    ListItem(
        textLeft = investment.name,
        subTextLeft = investment.category,

        textRight = investment.shares.toString(),
        subTextRight = annotateFinancePnL(
            text = "${investment.totalMarketPrice.format()} ${investment.currency.formatCurrency()}",
            pnl = investment.unrealisedPnL,
        ),

        onClick = { onInvestmentClick(investment) }
    )
}

@Composable
private fun AssetItem(
    asset: InvestmentAsset,
    colorIndex: Int,
    onClick: () -> Unit,
) {
    ListItem(
        textLeft = asset.name,
        subTextLeft = asset.category,

        textRight = asset.shares.toString(),
        subTextRight = annotateFinancePnL(
            text = "${asset.marketPrice.format()} ${asset.currency.formatCurrency()}",
            pnl = asset.unrealisedPnL,
        ),

        colorIndex = colorIndex,

        onClick = onClick,
    )
}

@Composable
private fun CategoryItem(
    category: InvestmentCategory,
    colorIndex: Int,
    onClick: () -> Unit,
) {
    ListItem(
        textLeft = category.name,
        subTextRight = category.totals.formatMarketPrice(),
        colorIndex = colorIndex,
        onClick = onClick,
    )
}

@AllDevicesPreview
@Composable
private fun InvestmentsScreenPreview(initialPage: Int = 0) {
    UrgatTheme {
        InvestmentsScreen(
            uiState = InvestmentsUiState(
                investments = SampleData.SAMPLE_INVESTMENTS_GROUPED,
                categoriesYearly = SampleData.SAMPLE_INVESTMENTS_CATEGORIES_YEARLY,
                assets = SampleData.SAMPLE_INVESTMENTS_ASSETS,
                categories = SampleData.SAMPLE_INVESTMENTS_CATEGORIES,
                netWorth = SampleData.SAMPLE_INVESTMENTS_NET_WORTH,
                filteringUiInfo = InvestmentsFilteringUiInfo(),
                isLoading = false,
                hasError = false,
                initialPage = initialPage,
            ),
            onIntent = {},
            onInvestmentClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun InvestmentsScreenYearlyPreview() {
    InvestmentsScreenPreview(initialPage = 1)
}

@AllDevicesPreview
@Composable
private fun InvestmentsScreenAllTimePreview() {
    InvestmentsScreenPreview(initialPage = 2)
}

@AllDevicesPreview
@Composable
private fun InvestmentsScreeAssetsPreview() {
    InvestmentsScreenPreview(initialPage = 3)
}

@AllDevicesPreview
@Composable
private fun InvestmentsScreenEmptyPreview() {
    UrgatTheme {
        InvestmentsScreen(
            uiState = InvestmentsUiState(
                investments = listOf(),
                netWorth = InvestmentNetWorth(totals = listOf()),
                categories = listOf(),
                assets = listOf(),
                filteringUiInfo = InvestmentsFilteringUiInfo(),
                isLoading = false,
                hasError = false,
            ),
            onIntent = {},
            onInvestmentClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun InvestmentsScreenLoadingPreview() {
    UrgatTheme {
        InvestmentsScreen(
            uiState = InvestmentsUiState(
                isLoading = true,
            ),
            onIntent = {},
            onInvestmentClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun InvestmentsScreenErrorPreview() {
    UrgatTheme {
        InvestmentsScreen(
            uiState = InvestmentsUiState(
                hasError = true,
            ),
            onIntent = {},
            onInvestmentClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}