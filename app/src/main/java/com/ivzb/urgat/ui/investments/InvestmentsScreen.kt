package com.ivzb.urgat.ui.investments

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentNetWorth
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.domain.investment.GroupedInvestments
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.FilterMenu
import com.ivzb.urgat.ui.components.ItemHeaderSmall
import com.ivzb.urgat.ui.components.ListItem
import com.ivzb.urgat.ui.components.MoreMenu
import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.components.SortMenu
import com.ivzb.urgat.ui.components.screens.PagerScreen
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.util.annotateCostPrice
import com.ivzb.urgat.util.annotateFinancePnL
import com.ivzb.urgat.util.annotateMarketPrice
import com.ivzb.urgat.util.isFirst
import com.ivzb.urgat.util.isLast
import com.ivzb.urgat.util.paddingIf
import com.ivzb.urgat.util.toggle

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
    // the selections are kept by name, instead of by id, so they survive switching between tabs,
    // as every tab groups its own items and generates new ids for them
    val expandedCategoryNames = rememberSaveable { mutableStateListOf<String>() }
    val expandedAssetNames = rememberSaveable { mutableStateListOf<String>() }
    var selectedChartTitle by rememberSaveable { mutableStateOf<String?>(null) }

    PagerScreen(
        uiState = uiState,

        route = InvestmentsRoute,
        searchQuery = uiState.filteringUiInfo.searchQuery,
        onSearchQueryChanged = { query -> onIntent(InvestmentsIntent.SearchQueryChanged(query)) },
        onBack = onBack,

        topAppBarActionsContent = {
            InvestmentsTopAppBarActions(
                uiState = uiState,
                onIntent = onIntent,
            )
        },

        content = { page, listState, onListStateChanged ->
            InvestmentsPageContent(
                page = page,
                uiState = uiState,
                onIntent = onIntent,
                expandedCategoryNames = expandedCategoryNames,
                expandedAssetNames = expandedAssetNames,
                onInvestmentClick = onInvestmentClick,
                selectedChartTitle = selectedChartTitle,
                onSelectedChartTitleChanged = { title -> selectedChartTitle = title },
                listState = listState,
                onListStateChanged = onListStateChanged,
            )
        },

        modifier = modifier,
    )
}

@Composable
private fun InvestmentsTopAppBarActions(
    uiState: InvestmentsUiState,
    onIntent: (InvestmentsIntent) -> Unit,
) {
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
}

@Composable
private fun InvestmentsPageContent(
    page: Page,
    uiState: InvestmentsUiState,
    onIntent: (InvestmentsIntent) -> Unit,
    expandedCategoryNames: MutableList<String>,
    expandedAssetNames: MutableList<String>,
    onInvestmentClick: (Investment) -> Unit,
    selectedChartTitle: String?,
    onSelectedChartTitleChanged: (String?) -> Unit,
    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {
    when (page) {
        InvestmentsPage.Yearly -> GroupedCategoriesPage(
            categories = uiState.categoriesYearly,
            averages = uiState.averagesYearly,
            filteringUiInfo = uiState.filteringUiInfo,
            expandedCategoryNames = expandedCategoryNames,
            onCategoryClick = { categoryName -> expandedCategoryNames.toggle(categoryName) },
            onInvestmentClick = onInvestmentClick,
            onResetFilters = { onIntent(InvestmentsIntent.ResetFilters) },
            selectedChartTitle = selectedChartTitle,
            onSelectedChartTitleChanged = onSelectedChartTitleChanged,
            onListStateChanged = onListStateChanged,
        )

        InvestmentsPage.AllTime -> CategoriesPage(
            group = stringResource(R.string.all_time),
            total = annotateMarketPrice(uiState.netWorth?.totals ?: listOf()),
            categories = uiState.categories,
            expandedCategoryNames = expandedCategoryNames,

            average = uiState.averageAllTime,

            filteringUiInfo = uiState.filteringUiInfo,
            onResetFilters = { onIntent(InvestmentsIntent.ResetFilters) },

            onCategoryClick = { categoryName -> expandedCategoryNames.toggle(categoryName) },
            onInvestmentClick = onInvestmentClick,

            selectedChartTitle = selectedChartTitle,
            onSelectedChartTitleChanged = onSelectedChartTitleChanged,

            listState = listState,
            onListStateChanged = onListStateChanged,
        )

        InvestmentsPage.Assets -> AssetsPage(
            group = stringResource(R.string.assets),
            total = annotateMarketPrice(uiState.netWorth?.totals ?: listOf()),

            assets = uiState.assets,
            expandedAssetNames = expandedAssetNames,

            average = uiState.averageAllTime,

            filteringUiInfo = uiState.filteringUiInfo,
            onResetFilters = { onIntent(InvestmentsIntent.ResetFilters) },

            onAssetClick = { assetName -> expandedAssetNames.toggle(assetName) },
            onInvestmentClick = onInvestmentClick,

            selectedChartTitle = selectedChartTitle,
            onSelectedChartTitleChanged = onSelectedChartTitleChanged,

            listState = listState,
            onListStateChanged = onListStateChanged,
        )
    }
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
                    total = annotateCostPrice(total),
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

internal fun LazyListScope.subInvestments(
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
        subTextLeft = investment.description ?: investment.category,

        textRight = investment.shares.toString(),
        subTextRight = annotateFinancePnL(
            text = investment.formatTotalMarketPrice(),
            pnl = investment.unrealisedPnL,
        ),

        onClick = { onInvestmentClick(investment) }
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
                averagesYearly = SampleData.SAMPLE_INVESTMENTS_AVERAGES_YEARLY,
                averageAllTime = SampleData.SAMPLE_INVESTMENTS_AVERAGE_ALL_TIME,
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
