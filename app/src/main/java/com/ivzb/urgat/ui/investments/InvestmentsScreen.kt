package com.ivzb.urgat.ui.investments

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import com.ivzb.urgat.util.paddingIf
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
        modifier = modifier
    ) { listState ->

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
                    listState = listState,
                )
            }
        }
    }
}

@Composable
private fun InvestmentsContent(
    uiState: InvestmentsUiState,
    onInvestmentClick: (Investment) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    val pages = INVESTMENT_PAGES
    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

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
                state = listState
            ) {

                when (pages[pageIndex]) {
                    InvestmentsPage.Transactions -> transactionsPage(
                        investments = uiState.investments,
                        onInvestmentClick = onInvestmentClick,
                    )

                    InvestmentsPage.Assets -> assetsPage(
                        assets = uiState.assets,
                    )

                    InvestmentsPage.Categories -> categoriesPage(
                        categories = uiState.categories,
                    )

                    InvestmentsPage.NetWorth -> netWorthPage(
                        netWorth = uiState.netWorth,
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
    investments.forEachIndexed { index, (group, totals, investments) ->
        item {
            ItemHeaderSmall(
                text = group,
                total = totals.joinToString(" | "),
                modifier = Modifier
                    .paddingIf(index == 0, bottom = dimensionResource(R.dimen.vertical_margin))
                    .paddingIf(index > 0, vertical = dimensionResource(R.dimen.vertical_margin))
                    .padding(horizontal = dimensionResource(R.dimen.list_item_padding))
            )
        }

        items(investments) { investment ->
            InvestmentItem(
                investment = investment,
                onInvestmentClick = onInvestmentClick
            )
        }
    }
}

private fun LazyListScope.assetsPage(
    assets: List<InvestmentAsset>,
) {
    items(assets) { asset ->
        AssetItem(
            asset = asset,
        )
    }
}

private fun LazyListScope.categoriesPage(
    categories: List<InvestmentCategory>,
) {
    items(categories) { category ->
        CategoryItem(
            category = category,
        )
    }
}

private fun LazyListScope.netWorthPage(
    netWorth: InvestmentNetWorth?,
) {
    if (netWorth != null) {
        item {
            InvestmentsNetWorth(netWorth)
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
) {
    ListItem(
        textLeft = asset.name,
        subTextLeft = asset.category,

        textRight = asset.shares.toString(),
        subTextRight = "${asset.marketValue.format()} ${asset.currency}",

        onClick = { /* NOOP */ }
    )
}

@Composable
private fun CategoryItem(
    category: InvestmentCategory,
) {
    ListItem(
        textLeft = category.name,
        subTextLeft = "",

        textRight = category.values.joinToString(" | "),
        subTextRight = "",

        onClick = { /* NOOP */ },
    )
}

@Composable
private fun InvestmentsNetWorth(
    netWorth: InvestmentNetWorth,
) {
    ListItem(
        textLeft = stringResource(R.string.total),
        subTextLeft = "",

        textRight = netWorth.values.joinToString(" | "),
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
                netWorth = InvestmentNetWorth(values = listOf()),
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