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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
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
import com.ivzb.urgat.ui.components.EmptyStateScreen
import com.ivzb.urgat.ui.components.FilterMenu
import com.ivzb.urgat.ui.components.ItemHeaderSmall
import com.ivzb.urgat.ui.components.ListDetailsScaffold
import com.ivzb.urgat.ui.components.ListItem
import com.ivzb.urgat.ui.components.LoadingState
import com.ivzb.urgat.ui.theme.UrgatTheme
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
    val pages = INVESTMENT_PAGES
    val pagerState = rememberPagerState { pages.size }
    val listStates = remember { List(pages.size) { LazyListState() } }
    val currentListState = remember { derivedStateOf { listStates[pagerState.currentPage] } }

    ListDetailsScaffold(
        selectedRoute = InvestmentsRoute,
        onSearchQueryChanged = { query -> onIntent(InvestmentsIntent.SearchQueryChanged(query)) },
        onAddItem = onAddInvestment,
        onBack = onBack,
        topAppBarActionsContent = {
            FilterMenu(
                items = uiState.filteringUiInfo.filterMenuItems,
                onFilter = { onIntent(InvestmentsIntent.SetFiltering(it)) },
            )
        },
        listState = currentListState.value,
        modifier = modifier
    ) {

        when {
            uiState.isLoading -> {
                LoadingState(
                    uiState.filteringUiInfo.loadingLabel
                )
            }

            uiState.hasError -> {
                EmptyStateScreen(
                    label = uiState.filteringUiInfo.errorLabel,
                    iconRes = uiState.filteringUiInfo.errorIconRes,
                )
            }

            uiState.investments.isEmpty() -> {
               EmptyStateScreen(
                   label = uiState.filteringUiInfo.emptyLabel,
                   iconRes = uiState.filteringUiInfo.emptyIconRes
               )
            }

            else -> {
                InvestmentsContent(
                    uiState = uiState,
                    onInvestmentClick = onInvestmentClick,
                    pages = pages,
                    pagerState = pagerState,
                    listStates = listStates,
                )
            }
        }
    }
}

@Composable
private fun InvestmentsContent(
    uiState: InvestmentsUiState,
    onInvestmentClick: (Investment) -> Unit,
    pages: List<InvestmentsPage>,
    pagerState: PagerState,
    listStates: List<LazyListState>,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val expandedYearlyIds = rememberSaveable { mutableStateSetOf<String>() }
    val expandedAssetIds = rememberSaveable { mutableStateSetOf<String>() }
    val expandedCategoryIds = rememberSaveable { mutableStateSetOf<String>() }

    Column {
        PrimaryScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 0.dp
        ) {
            pages.forEachIndexed { index, page ->
                Tab(
                    text = {
                        Text(
                            text = stringResource(page.titleResId),
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
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->

            LazyColumn(
                modifier = modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(
                    horizontal = dimensionResource(R.dimen.horizontal_margin),
                    vertical = dimensionResource(R.dimen.vertical_margin),
                ),
                state = listStates[pageIndex]
            ) {

                when (pages[pageIndex]) {
                    InvestmentsPage.Transactions -> transactionsPage(
                        investments = uiState.investments,
                        onInvestmentClick = onInvestmentClick,
                    )

                    InvestmentsPage.Yearly -> groupedCategoriesPage(
                        categories = uiState.categoriesYearly,
                        expandedCategoryIds = expandedYearlyIds,
                        onCategoryClick = { categoryId -> expandedYearlyIds.toggle(categoryId) },
                        onInvestmentClick = onInvestmentClick,
                    )

                    InvestmentsPage.NetWorth -> netWorthPage(
                        netWorth = uiState.netWorth,

                        categories = uiState.categories,
                        expandedCategoryIds = expandedCategoryIds,
                        onCategoryClick = { categoryId -> expandedCategoryIds.toggle(categoryId) },

                        assets = uiState.assets,
                        expandedAssetIds = expandedAssetIds,
                        onAssetClick = { assetId -> expandedAssetIds.toggle(assetId) },

                        onInvestmentClick = onInvestmentClick,
                    )
                }

                item {
                    Spacer(
                        modifier = Modifier
                            .height(dimensionResource(R.dimen.vertical_margin))
                    )
                }
            }
        }
    }
}

private fun LazyListScope.transactionsPage(
    investments: List<GroupedInvestments>,
    onInvestmentClick: (Investment) -> Unit,
) {
    investments.forEachIndexed { index, (group, total, investments) ->
        item {
            ItemHeaderSmall(
                text = group,
                total = total,
                modifier = Modifier
                    .paddingIf(index.isFirst(), bottom = dimensionResource(R.dimen.vertical_margin))
                    .paddingIf(!index.isFirst(), vertical = dimensionResource(R.dimen.vertical_margin))
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

private fun LazyListScope.groupedCategoriesPage(
    categories: List<GroupedInvestmentCategories>,
    expandedCategoryIds: Set<String>,
    onCategoryClick: (String) -> Unit,
    onInvestmentClick: (Investment) -> Unit,
) {
    categories.forEachIndexed { index, (group, total, categories) ->
        item {
            ItemHeaderSmall(
                text = group,
                total = total,
                modifier = Modifier
                    .paddingIf(index == 0, bottom = dimensionResource(R.dimen.vertical_margin))
                    .paddingIf(index > 0, vertical = dimensionResource(R.dimen.vertical_margin))
                    .padding(horizontal = dimensionResource(R.dimen.list_item_padding))
            )
        }

        categories.forEach { category ->
            item(key = category.id) {
                CategoryItem(
                    category = category,
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

private fun LazyListScope.netWorthPage(
    netWorth: InvestmentNetWorth?,

    categories: List<InvestmentCategory>,
    expandedCategoryIds: Set<String>,
    onCategoryClick: (String) -> Unit,

    assets: List<InvestmentAsset>,
    expandedAssetIds: Set<String>,
    onAssetClick: (String) -> Unit,

    onInvestmentClick: (Investment) -> Unit,
) {

    item {
        ItemHeaderSmall(
            text = stringResource(R.string.net_worth),
            modifier = Modifier
                .padding(bottom = dimensionResource(R.dimen.list_item_padding))
        )
    }

    if (netWorth != null) {
        item {
            InvestmentsNetWorth(netWorth)
        }
    }

    item {
        ItemHeaderSmall(
            text = stringResource(R.string.categories),
            modifier = Modifier
                .padding(vertical = dimensionResource(R.dimen.list_item_padding))
        )
    }

    categories.forEach { category ->
        item(key = category.id) {
            CategoryItem(
                category = category,
                onClick = { onCategoryClick(category.id) },
            )
        }

        val isExpanded = expandedCategoryIds.contains(category.id)

        if (isExpanded) {
            subInvestments(category.investments, onInvestmentClick)
        }
    }

    item {
        ItemHeaderSmall(
            text = stringResource(R.string.assets),
            modifier = Modifier
                .padding(vertical = dimensionResource(R.dimen.list_item_padding))
        )
    }

    assets.forEach { asset ->
        item(key = asset.id) {
            AssetItem(
                asset = asset,
                onClick = { onAssetClick(asset.id) },
            )
        }

        val isExpanded = expandedAssetIds.contains(asset.id)

        if (isExpanded) {
            subInvestments(asset.investments, onInvestmentClick)
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
    onClick: () -> Unit,
) {
    ListItem(
        textLeft = asset.name,
        subTextLeft = asset.category,

        textRight = asset.shares.toString(),
        subTextRight = "${asset.marketValue.format()} ${asset.currency}",

        onClick = onClick,
    )
}

@Composable
private fun CategoryItem(
    category: InvestmentCategory,
    onClick: () -> Unit,
) {
    ListItem(
        textLeft = category.name,
        subTextLeft = "",

        textRight = category.total,
        subTextRight = "",

        onClick = onClick,
    )
}

@Composable
private fun InvestmentsNetWorth(
    netWorth: InvestmentNetWorth,
) {
    ListItem(
        textLeft = stringResource(R.string.total),
        subTextLeft = "",

        textRight = netWorth.total,
        subTextRight = "",

        onClick = { /* NOOP */ },
    )
}

@AllDevicesPreview
@Composable
private fun InvestmentsScreenPreview() {
    UrgatTheme {
        InvestmentsScreen(
            uiState = InvestmentsUiState(
                investments = SampleData.SAMPLE_INVESTMENTS_GROUPED,
                netWorth = SampleData.SAMPLE_INVESTMENTS_NET_WORTH,
                categories = SampleData.SAMPLE_INVESTMENTS_CATEGORIES,
                assets = SampleData.SAMPLE_INVESTMENTS_ASSETS,
                filteringUiInfo = InvestmentsFilteringUiInfo(),
                isLoading = false,
                hasError = false
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