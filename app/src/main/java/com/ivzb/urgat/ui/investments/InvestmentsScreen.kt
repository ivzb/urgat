package com.ivzb.urgat.ui.investments

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentAsset
import com.ivzb.urgat.data.investment.InvestmentCategory
import com.ivzb.urgat.data.investment.InvestmentNetWorth
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.ui.GroupedItems
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.EmptyStateScreen
import com.ivzb.urgat.ui.components.FilterMenu
import com.ivzb.urgat.ui.components.ItemHeaderLarge
import com.ivzb.urgat.ui.components.ItemHeaderMedium
import com.ivzb.urgat.ui.components.ItemHeaderSmall
import com.ivzb.urgat.ui.components.ListDetailsScaffold
import com.ivzb.urgat.ui.components.ListItem
import com.ivzb.urgat.ui.components.LoadingState
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.util.format

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
                    investments = uiState.investments,
                    netWorth = uiState.netWorth,
                    categories = uiState.categories,
                    assets = uiState.assets,
                    onInvestmentClick = onInvestmentClick,
                    filteringUiInfo = uiState.filteringUiInfo,
                    listState = listState,
                )
            }
        }
    }
}

@Composable
private fun InvestmentsContent(
    investments: List<GroupedItems<String, Investment>>,
    netWorth: List<InvestmentNetWorth>,
    categories: List<InvestmentCategory>,
    assets: List<InvestmentAsset>,
    onInvestmentClick: (Investment) -> Unit,

    filteringUiInfo: InvestmentsFilteringUiInfo,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = dimensionResource(R.dimen.content_padding_horizontal),
            vertical = dimensionResource(R.dimen.content_padding_vertical),
        ),
        state = listState
    ) {

        item {
            ItemHeaderLarge(text = stringResource(filteringUiInfo.currentFilteringLabel))
        }

        item {
            ItemHeaderMedium(
                text = stringResource(R.string.net_worth),
                modifier = Modifier
                    .padding(vertical = dimensionResource(R.dimen.vertical_margin))
            )
        }

        items(netWorth) { netWorth ->
            InvestmentsNetWorth(netWorth)
        }

        item {
            ItemHeaderMedium(
                text = stringResource(R.string.categories),
                modifier = Modifier
                    .padding(vertical = dimensionResource(R.dimen.vertical_margin))
            )
        }

        items(categories) { category ->
            CategoryItem(
                category = category,
            )
        }

        item {
            ItemHeaderMedium(
                text = stringResource(R.string.assets),
                modifier = Modifier
                    .padding(vertical = dimensionResource(R.dimen.vertical_margin))
            )
        }

        items(assets) { asset ->
            AssetItem(
                asset = asset,
            )
        }

        item {
            ItemHeaderMedium(
                text = stringResource(R.string.transactions),
                modifier = Modifier
                    .padding(top = dimensionResource(R.dimen.vertical_margin))
            )
        }

        investments.forEach { (group, investments) ->
            item {
                ItemHeaderSmall(
                    text = group,
                    modifier = Modifier
                        .padding(vertical = dimensionResource(R.dimen.vertical_margin))
                )
            }

            items(investments) { investment ->
                InvestmentItem(
                    investment = investment,
                    onInvestmentClick = onInvestmentClick
                )
            }
        }

        item {
            Spacer(
                modifier = Modifier
                    .height(dimensionResource(R.dimen.vertical_margin))
            )
        }
    }
}

@Composable
private fun InvestmentsNetWorth(
    netWorth: InvestmentNetWorth,
) {
    ListItem(
        textLeft = "",
        subTextLeft = "",

        textRight = netWorth.marketValue.format(),
        subTextRight = netWorth.currency,

        onClick = { /* NOOP */ },
    )
}

@Composable
private fun CategoryItem(
    category: InvestmentCategory,
) {
    ListItem(
        textLeft = category.name,
        subTextLeft = "",

        textRight = category.marketValue.format(),
        subTextRight = category.currency,

        onClick = { /* NOOP */ },
    )
}

@Composable
fun AssetItem(
    asset: InvestmentAsset,
) {
    ListItem(
        textLeft = asset.name,
        subTextLeft = asset.category,

        textRight = asset.marketValue.format(),
        subTextRight = asset.currency,

        onClick = { /* NOOP */ }
    )
}

@Composable
fun InvestmentItem(
    investment: Investment,
    onInvestmentClick: (Investment) -> Unit
) {
    ListItem(
        textLeft = investment.name,
        subTextLeft = investment.category,

        textRight = investment.marketValue.format(),
        subTextRight = investment.currency,

        onClick = { onInvestmentClick(investment) }
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
                netWorth = listOf(),
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