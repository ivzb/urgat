package com.ivzb.urgat.ui.investments

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.EmptyStateScreen
import com.ivzb.urgat.ui.components.FilterMenu
import com.ivzb.urgat.ui.components.ItemHeader
import com.ivzb.urgat.ui.components.ListDetailsScaffold
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

            else -> {
                InvestmentsContent(
                    investments = uiState.items,
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
    investments: List<Investment>,
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
            ItemHeader(title = stringResource(filteringUiInfo.currentFilteringLabel))
        }

        item {
            Spacer(modifier = Modifier
                .height(dimensionResource(R.dimen.vertical_margin))
            )
        }

        items(investments) { investment ->
            InvestmentItem(
                investment = investment,
                onInvestmentClick = onInvestmentClick
            )
        }

        if (investments.isEmpty()) {
            item {
                EmptyStateScreen(
                    label = filteringUiInfo.emptyLabel,
                    iconRes = filteringUiInfo.emptyIconRes
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
fun InvestmentItem(
    investment: Investment,
    onInvestmentClick: (Investment) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onInvestmentClick(investment) }
                .padding(dimensionResource(R.dimen.list_item_padding)),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column (
                horizontalAlignment = Alignment.Start
            ) {

                Text(
                    text = investment.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall,
                )

                Text(
                    text = investment.category,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column (
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = investment.amount.toString(),
                    style = MaterialTheme.typography.bodySmall,
                )

                Text(
                    text = investment.currency,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@AllDevicesPreview
@Composable
private fun InvestmentsScreenPreview() {
    UrgatTheme {
        InvestmentsScreen(
            uiState = InvestmentsUiState(
                items = SampleData.SAMPLE_INVESTMENTS,
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
                items = SampleData.SAMPLE_INVESTMENTS,
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