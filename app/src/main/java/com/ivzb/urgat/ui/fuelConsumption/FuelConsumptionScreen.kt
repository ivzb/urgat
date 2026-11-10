package com.ivzb.urgat.ui.fuelConsumption

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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.fuelConsumption.FuelConsumption
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionTotal.Companion.sumByType
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.domain.fuelConsumption.GroupedFuelConsumption
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.FilterMenu
import com.ivzb.urgat.ui.components.ItemHeaderSmall
import com.ivzb.urgat.ui.components.ListItem
import com.ivzb.urgat.ui.components.MoreMenu
import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.components.SortMenu
import com.ivzb.urgat.ui.components.screens.PagerScreen
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.util.annotateFuelConsumptionTotals
import com.ivzb.urgat.util.format
import com.ivzb.urgat.util.paddingIf
import com.ivzb.urgat.util.toLocalDate
import com.ivzb.urgat.util.toggle

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
            FuelConsumptionTopAppBarActions(
                uiState = uiState,
                onIntent = onIntent,
            )
        },

        content = { page, listState, onListStateChanged ->
            FuelConsumptionPageContent(
                page = page,
                uiState = uiState,
                onIntent = onIntent,
                expandedMonthlyIds = expandedMonthlyIds,
                expandedYearlyIds = expandedYearlyIds,
                expandedAllTimeIds = expandedAllTimeIds,
                onFuelConsumptionClick = onFuelConsumptionClick,
                listState = listState,
                onListStateChanged = onListStateChanged,
            )
        },

        modifier = modifier,
    )
}

@Composable
private fun FuelConsumptionTopAppBarActions(
    uiState: FuelConsumptionUiState,
    onIntent: (FuelConsumptionIntent) -> Unit,
) {
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
}

@Composable
private fun FuelConsumptionPageContent(
    page: Page,
    uiState: FuelConsumptionUiState,
    onIntent: (FuelConsumptionIntent) -> Unit,
    expandedMonthlyIds: MutableList<String>,
    expandedYearlyIds: MutableList<String>,
    expandedAllTimeIds: MutableList<String>,
    onFuelConsumptionClick: (FuelConsumption) -> Unit,
    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {
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
