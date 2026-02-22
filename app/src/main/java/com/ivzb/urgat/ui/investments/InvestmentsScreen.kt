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
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentAsset
import com.ivzb.urgat.data.investment.InvestmentCategory
import com.ivzb.urgat.data.investment.InvestmentNetWorth
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.domain.investment.GroupedInvestmentCategories
import com.ivzb.urgat.domain.investment.GroupedInvestments
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.FilterMenu
import com.ivzb.urgat.ui.components.ItemHeaderSmall
import com.ivzb.urgat.ui.components.ListItem
import com.ivzb.urgat.ui.components.SortMenu
import com.ivzb.urgat.ui.components.charts.PieChart
import com.ivzb.urgat.ui.components.charts.PieChartItem
import com.ivzb.urgat.ui.components.format.formatInvestmentsChart
import com.ivzb.urgat.ui.components.screens.PagerScreen
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.util.annotateFormattedString
import com.ivzb.urgat.util.format
import com.ivzb.urgat.util.isFirst
import com.ivzb.urgat.util.isLast
import com.ivzb.urgat.util.paddingIf
import com.ivzb.urgat.util.toggle
import kotlinx.coroutines.launch

@Composable
fun InvestmentsScreen(
    viewModel: InvestmentsViewModel,

    onAddInvestment: () -> Unit,
    onInvestmentClick: (Investment) -> Unit,
    onBack: () -> Unit,

    modifier: Modifier = Modifier,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    InvestmentsScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onAddInvestment = onAddInvestment,
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

    onAddInvestment: () -> Unit,
    onInvestmentClick: (Investment) -> Unit,
    onBack: () -> Unit,

    modifier: Modifier = Modifier,
) {
    val expandedYearlyIds = rememberSaveable { mutableStateSetOf<String>() }
    val expandedAssetIds = rememberSaveable { mutableStateSetOf<String>() }
    val expandedCategoryIds = rememberSaveable { mutableStateSetOf<String>() }

    PagerScreen(
        uiState = uiState,

        route = InvestmentsRoute,
        onSearchQueryChanged = { query -> onIntent(InvestmentsIntent.SearchQueryChanged(query)) },
        onAddItem = onAddInvestment,
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
        },

        content = { page, listState, onListStateChanged ->
            when (page) {
                InvestmentsPage.Yearly -> GroupedCategoriesPage(
                    categories = uiState.categoriesYearly,
                    expandedCategoryIds = expandedYearlyIds,
                    onCategoryClick = { categoryId -> expandedYearlyIds.toggle(categoryId) },
                    onInvestmentClick = onInvestmentClick,

                    onListStateChanged = onListStateChanged,
                )

                InvestmentsPage.AllTime -> CategoriesPage(
                    group = stringResource(R.string.all_time),
                    total = uiState.netWorth?.total.orEmpty(),
                    categories = uiState.categories,
                    expandedCategoryIds = expandedCategoryIds,

                    onCategoryClick = { categoryId -> expandedCategoryIds.toggle(categoryId) },
                    onInvestmentClick = onInvestmentClick,

                    listState = listState,
                    onListStateChanged = onListStateChanged,
                )

                InvestmentsPage.Assets -> AssetsPage(
                    group = stringResource(R.string.assets),
                    total = uiState.netWorth?.total.orEmpty(),

                    assets = uiState.assets,
                    expandedAssetIds = expandedAssetIds,
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
                    total = total,
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
    expandedCategoryIds: Set<String>,
    onCategoryClick: (String) -> Unit,
    onInvestmentClick: (Investment) -> Unit,
    onListStateChanged: (LazyListState) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState { categories.size }
    val nestedListStates = remember(categories.size) {
        List(categories.size) { LazyListState() }
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
                total = category.total,
                categories = category.items,
                expandedCategoryIds = expandedCategoryIds,

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
    total: String,

    categories: List<InvestmentCategory>,
    expandedCategoryIds: Set<String>,

    onCategoryClick: (String) -> Unit,
    onInvestmentClick: (Investment) -> Unit,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    LaunchedEffect(Unit) {
        onListStateChanged(listState)
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
                    value = it.investments.sumOf { it.marketValue }.toFloat(),
                    selectedText = formatInvestmentsChart(it.name, it.total)
                )
            }

            val defaultText = formatInvestmentsChart(group, total)

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
    total: String,

    assets: List<InvestmentAsset>,
    expandedAssetIds: Set<String>,
    onAssetClick: (String) -> Unit,

    onInvestmentClick: (Investment) -> Unit,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    LaunchedEffect(Unit) {
        onListStateChanged(listState)
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
            @Composable
            fun formattedText(name: String, total: String): AnnotatedString {
                val totalFormatted = total.split(Investment.SEPARATOR).joinToString("\n")
                val textFormatted = stringResource(R.string.chart_text_format, name, totalFormatted).trim()

                return annotateFormattedString(
                    formattedString = textFormatted,
                    part = totalFormatted,
                    spanStyle = SpanStyle(
                        fontWeight = FontWeight.Bold
                    ),
                )
            }

            val data = assets.map {
                PieChartItem(
                    value = it.marketValue.toFloat(),
                    selectedText = formattedText(it.name, it.total)
                )
            }

            val defaultText = formattedText(group, total)

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
        subTextRight = "${investment.marketValue.format()} ${investment.currency}",

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
        subTextRight = "${asset.marketValue.format()} ${asset.currency}",

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
        subTextLeft = "",

        textRight = category.total,
        subTextRight = "",

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
            onAddInvestment = {},
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
                netWorth = InvestmentNetWorth(total = ""),
                categories = listOf(),
                assets = listOf(),
                filteringUiInfo = InvestmentsFilteringUiInfo(),
                isLoading = false,
                hasError = false,
            ),
            onIntent = {},
            onAddInvestment = {},
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
            onAddInvestment = {},
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
            onAddInvestment = {},
            onInvestmentClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}