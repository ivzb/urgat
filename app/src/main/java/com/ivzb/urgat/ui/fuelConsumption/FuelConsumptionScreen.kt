package com.ivzb.urgat.ui.fuelConsumption

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
import com.ivzb.urgat.data.fuelConsumption.FuelConsumption
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionCategory
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionTotal.Companion.sumByType
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.domain.fuelConsumption.GroupedFuelConsumptionCategories
import com.ivzb.urgat.domain.fuelConsumption.GroupedFuelConsumption
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
import com.ivzb.urgat.util.annotateChart
import com.ivzb.urgat.util.annotateFuelConsumptionTotals
import com.ivzb.urgat.util.format
import com.ivzb.urgat.util.isFirst
import com.ivzb.urgat.util.isLast
import com.ivzb.urgat.util.paddingIf
import com.ivzb.urgat.util.toLocalDate
import com.ivzb.urgat.util.toggle
import kotlinx.coroutines.launch

@Composable
fun FuelConsumptionScreen(
    viewModel: FuelConsumptionViewModel,

    onFuelConsumptionClick: (FuelConsumption) -> Unit,
    onBack: () -> Unit,

    modifier: Modifier = Modifier,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FuelConsumptionScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onFuelConsumptionClick = onFuelConsumptionClick,
        onBack = onBack,
        modifier = modifier
    )
}

@VisibleForTesting
@Composable
fun FuelConsumptionScreen(
    uiState: FuelConsumptionUiState,
    onIntent: (FuelConsumptionIntent) -> Unit,

    onFuelConsumptionClick: (FuelConsumption) -> Unit,

    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    val expandedMonthlyIds = rememberSaveable { mutableStateListOf<String>() }
    val expandedYearlyIds = rememberSaveable { mutableStateListOf<String>() }
    val expandedAllTimeIds = rememberSaveable { mutableStateListOf<String>() }

    PagerScreen(
        uiState = uiState,

        route = FuelConsumptionRoute,
        searchQuery = uiState.filteringUiInfo.searchQuery,
        onSearchQueryChanged = { query -> onIntent(FuelConsumptionIntent.SearchQueryChanged(query)) },
        onBack = onBack,

        topAppBarActionsContent = {
            FilterMenu(
                items = uiState.filteringUiInfo.filterMenuItems,
                showBadge = uiState.filteringUiInfo.showFilterBadge,
                onFilter = { onIntent(FuelConsumptionIntent.SetFiltering(it)) },
            )

            FilterMenu(
                items = uiState.filteringUiInfo.carFilterMenuItems,
                showBadge = uiState.filteringUiInfo.showCarFilterBadge,
                onFilter = { onIntent(FuelConsumptionIntent.SetCarFiltering(it)) },
                iconResId = R.drawable.ic_filter_car,
                contentDescriptionResId = R.string.menu_filter_car,
            )

            SortMenu(
                items = uiState.filteringUiInfo.sortMenuItems,
                showBadge = uiState.filteringUiInfo.showSortBadge,
                onFilter = { onIntent(FuelConsumptionIntent.SetSorting(it)) },
            )

            MoreMenu(
                listOfNotNull(
                    (R.string.reset_filters to { onIntent(FuelConsumptionIntent.ResetFilters) }).takeIf { uiState.filteringUiInfo.hasAppliedFilters },
                )
            )
        },

        content = { page, listState, onListStateChanged ->
            when (page) {
                FuelConsumptionPage.Monthly -> GroupedCategoriesPage(
                    categories = uiState.categoriesMonthly,
                    filteringUiInfo = uiState.filteringUiInfo,
                    expandedCategoryIds = expandedMonthlyIds,
                    onCategoryClick = { categoryId -> expandedMonthlyIds.toggle(categoryId) },
                    onFuelConsumptionClick = onFuelConsumptionClick,
                    onResetFilters = { onIntent(FuelConsumptionIntent.ResetFilters) },
                    onListStateChanged = onListStateChanged,
                )

                FuelConsumptionPage.Yearly -> GroupedCategoriesPage(
                    categories = uiState.categoriesYearly,
                    filteringUiInfo = uiState.filteringUiInfo,
                    expandedCategoryIds = expandedYearlyIds,
                    onCategoryClick = { categoryId -> expandedYearlyIds.toggle(categoryId) },
                    onFuelConsumptionClick = onFuelConsumptionClick,
                    onResetFilters = { onIntent(FuelConsumptionIntent.ResetFilters) },
                    onListStateChanged = onListStateChanged,
                )

                FuelConsumptionPage.AllTime -> CategoriesPage(
                    group = stringResource(R.string.all_time),
                    total = annotateFuelConsumptionTotals(
                        uiState.categoriesAllTime
                            .flatMap { it.totals }
                            .sumByType()
                    ),
                    categories = uiState.categoriesAllTime,
                    expandedCategoryIds = expandedAllTimeIds,
                    listState = listState,
                    onCategoryClick = { categoryId -> expandedAllTimeIds.toggle(categoryId) },
                    onFuelConsumptionClick = onFuelConsumptionClick,
                    filteringUiInfo = uiState.filteringUiInfo,
                    onResetFilters = { onIntent(FuelConsumptionIntent.ResetFilters) },
                    onListStateChanged = onListStateChanged,
                )
            }
        },

        modifier = modifier,
    )
}

@Composable
fun TransactionsPage(
    fuelConsumption: List<GroupedFuelConsumption>,
    onFuelConsumptionClick: (FuelConsumption) -> Unit,

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

        fuelConsumption.forEachIndexed { index, (group, total, fuelConsumption) ->
            item {
                ItemHeaderSmall(
                    text = group,
                    total = annotateFuelConsumptionTotals(total),
                    modifier = Modifier
                        .paddingIf(index == 0, bottom = dimensionResource(R.dimen.vertical_margin))
                        .paddingIf(index > 0, vertical = dimensionResource(R.dimen.vertical_margin))
                        .padding(horizontal = dimensionResource(R.dimen.list_item_padding))
                )
            }

            items(
                items = fuelConsumption,
                key = { fuelConsumption -> fuelConsumption.id },
            ) { fuelConsumption ->

                FuelConsumptionItem(
                    fuelConsumption = fuelConsumption,
                    onFuelConsumptionClick = onFuelConsumptionClick,
                )
            }
        }
    }
}

@Composable
private fun GroupedCategoriesPage(
    categories: List<GroupedFuelConsumptionCategories>,
    filteringUiInfo: FilteringUiInfo,
    expandedCategoryIds: List<String>,
    onCategoryClick: (String) -> Unit,
    onFuelConsumptionClick: (FuelConsumption) -> Unit,
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
                total = annotateFuelConsumptionTotals(category.total),

                categories = category.items,
                expandedCategoryIds = expandedCategoryIds,

                filteringUiInfo = filteringUiInfo,
                onResetFilters = onResetFilters,

                onCategoryClick = onCategoryClick,
                onFuelConsumptionClick = onFuelConsumptionClick,

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

    categories: List<FuelConsumptionCategory>,
    expandedCategoryIds: List<String>,

    filteringUiInfo: FilteringUiInfo,
    onResetFilters: () -> Unit,

    onCategoryClick: (String) -> Unit,
    onFuelConsumptionClick: (FuelConsumption) -> Unit,

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
                        value = it.fuelConsumption.sumOf { it.amount }.toFloat(),
                        title = it.name,
                        total = annotateFuelConsumptionTotals(it.totals),
                    )
                }
                defaultText = annotateChart(group, total)
            } else {
                // expanded category pie chart
                data = expandedCategory.fuelConsumption
                    .groupBy { it.type }
                    .map { (name, fuelConsumption) ->
                        val fuelConsumptionTotal = FuelConsumption.total(fuelConsumption)

                        PieChartItem(
                            value = fuelConsumption.sumOf { it.amount }.toFloat(),
                            title = name,
                            total = annotateFuelConsumptionTotals(fuelConsumptionTotal),
                        )
                    }
                defaultText = annotateChart(expandedCategory.name, annotateFuelConsumptionTotals(expandedCategory.totals))
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
                subFuelConsumption(category.fuelConsumption, onFuelConsumptionClick)
            }
        }
    }
}

private fun LazyListScope.subFuelConsumption(
    fuelConsumption: List<FuelConsumption>,
    onFuelConsumptionClick: (FuelConsumption) -> Unit,
) {
    itemsIndexed(
        fuelConsumption,
        key = { _, fuelConsumption -> fuelConsumption.id },
    ) { index, item ->

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.horizontal_margin))
                .paddingIf(
                    condition = index.isFirst(),
                    top = dimensionResource(R.dimen.expanded_group_vertical_margin)
                )
                .paddingIf(
                    condition = index.isLast(fuelConsumption.size),
                    bottom = dimensionResource(R.dimen.expanded_group_vertical_margin)
                ),
            shadowElevation = 2.dp,
        ) {

            FuelConsumptionItem(
                fuelConsumption = item,
                onFuelConsumptionClick = onFuelConsumptionClick,
            )
        }
    }
}

@Composable
fun FuelConsumptionItem(
    fuelConsumption: FuelConsumption,
    onFuelConsumptionClick: (FuelConsumption) -> Unit,
) {
    val date = remember { fuelConsumption.date.toLocalDate() }

    ListItem(
        textLeft = date.dayOfMonth.toString(),
        subTextLeft = date.month.name.take(3),

        textRight = fuelConsumption.carName,
        subTextRight = fuelConsumption.summary,

        onClick = { onFuelConsumptionClick(fuelConsumption) }
    )
}

@Composable
private fun CategoryItem(
    category: FuelConsumptionCategory,
    colorIndex: Int,
    onClick: () -> Unit,
) {

    ListItem(
        textLeft = category.name,
        subTextLeft = "",

        textRight = "",
        subTextRight = annotateFuelConsumptionTotals(category.totals),

        colorIndex = colorIndex,

        onClick = onClick,
    )
}

@AllDevicesPreview
@Composable
private fun FuelConsumptionScreenPreview(initialPage: Int = 0) {
    UrgatTheme {
        FuelConsumptionScreen(
            uiState = FuelConsumptionUiState(
                fuelConsumption = SampleData.SAMPLE_FUEL_CONSUMPTION_GROUPED,
                categoriesMonthly = SampleData.SAMPLE_FUEL_CONSUMPTION_CATEGORIES_MONTHLY,
                categoriesYearly = SampleData.SAMPLE_FUEL_CONSUMPTION_CATEGORIES_YEARLY,
                categoriesAllTime = SampleData.SAMPLE_FUEL_CONSUMPTION_CATEGORIES_ALL_TIME,
                filteringUiInfo = FuelConsumptionFilteringUiInfo(),
                isLoading = false,
                hasError = false,
                initialPage = initialPage,
            ),
            onIntent = {},
            onFuelConsumptionClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun FuelConsumptionScreenMonthlyPreview() {
    FuelConsumptionScreenPreview(initialPage = 1)
}

@AllDevicesPreview
@Composable
private fun FuelConsumptionScreenYearlyPreview() {
    FuelConsumptionScreenPreview(initialPage = 2)
}

@AllDevicesPreview
@Composable
private fun FuelConsumptionScreenAllTimePreview() {
    FuelConsumptionScreenPreview(initialPage = 3)
}

@AllDevicesPreview
@Composable
private fun FuelConsumptionScreenEmptyPreview() {
    UrgatTheme {
        FuelConsumptionScreen(
            uiState = FuelConsumptionUiState(
                fuelConsumption = listOf(),
                categoriesMonthly = listOf(),
                filteringUiInfo = FuelConsumptionFilteringUiInfo(),
                isLoading = false,
                hasError = false
            ),
            onIntent = {},
            onFuelConsumptionClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun FuelConsumptionScreenLoadingPreview() {
    UrgatTheme {
        FuelConsumptionScreen(
            uiState = FuelConsumptionUiState(
                fuelConsumption = listOf(),
                categoriesMonthly = listOf(),
                filteringUiInfo = FuelConsumptionFilteringUiInfo(),
                isLoading = true,
                hasError = false
            ),
            onIntent = {},
            onFuelConsumptionClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun FuelConsumptionScreenErrorPreview() {
    UrgatTheme {
        FuelConsumptionScreen(
            uiState = FuelConsumptionUiState(
                fuelConsumption = listOf(),
                categoriesMonthly = listOf(),
                filteringUiInfo = FuelConsumptionFilteringUiInfo(),
                isLoading = false,
                hasError = true
            ),
            onIntent = {},
            onFuelConsumptionClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}