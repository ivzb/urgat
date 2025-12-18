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

            uiState.items.isEmpty() -> {
               EmptyStateScreen(
                   label = uiState.filteringUiInfo.emptyLabel,
                   iconRes = uiState.filteringUiInfo.emptyIconRes
               )
            }

            else -> {
                InvestmentsContent(
                    groupedInvestments = uiState.items,
                    netWorth = uiState.netWorth,
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
    groupedInvestments: List<GroupedItems<String, Investment>>,
    netWorth: List<InvestmentNetWorth>,
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

        groupedInvestments.forEach { (group, investments) ->
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

        textRight = netWorth.amount.toString(),
        subTextRight = netWorth.currency,

        onClick = { /* NOOP */ },
    )
}

@Composable
fun AssetItem(
    asset: InvestmentAsset,
) {
    ListItem(
        textLeft = asset.category,
        subTextLeft = "",

        textRight = asset.amount.toString(),
        subTextRight = asset.currency,

        onClick = { /* NOOP */ },
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

        textRight = investment.amount.toString(),
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
                items = SampleData.SAMPLE_INVESTMENTS_GROUPED,
                netWorth = SampleData.SAMPLE_INVESTMENTS_NET_WORTH,
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
                items = listOf(),
                netWorth = listOf(),
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
                items = listOf(),
                filteringUiInfo = InvestmentsFilteringUiInfo(),
                isLoading = true,
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
private fun InvestmentsScreenErrorPreview() {
    UrgatTheme {
        InvestmentsScreen(
            uiState = InvestmentsUiState(
                items = listOf(),
                filteringUiInfo = InvestmentsFilteringUiInfo(),
                isLoading = false,
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