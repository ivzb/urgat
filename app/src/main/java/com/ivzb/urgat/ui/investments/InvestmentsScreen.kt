package com.ivzb.urgat.ui.investments

import android.content.res.Configuration
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.navigation.InvestmentsDestination
import com.ivzb.urgat.ui.components.EmptyStateScreen
import com.ivzb.urgat.ui.components.FilterMenu
import com.ivzb.urgat.ui.components.FilteringUiInfo
import com.ivzb.urgat.ui.components.Header
import com.ivzb.urgat.ui.components.HeaderItem
import com.ivzb.urgat.ui.components.LandscapePreview
import com.ivzb.urgat.ui.components.LoadingState
import com.ivzb.urgat.ui.components.UrgatScaffold
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun InvestmentsScreen(
    onAddInvestment: () -> Unit,
    onInvestmentClick: (Investment) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InvestmentsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    InvestmentsScreenScaffold(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onInvestmentClick = onInvestmentClick,
        onBack = onBack,
        modifier = modifier
    )

    LaunchedEffect(uiState.uiEvent) {
        uiState.uiEvent?.let { event ->
            when (event) {
                is InvestmentsUiEvent.AddInvestment -> onAddInvestment()
            }

            viewModel.onIntent(InvestmentsIntent.EventConsumed)
        }
    }
}

@Composable
private fun InvestmentsScreenScaffold(
    uiState: InvestmentsUiState,
    onIntent: (InvestmentsIntent) -> Unit,
    onInvestmentClick: (Investment) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {

    UrgatScaffold(
        selectedDestination = InvestmentsDestination,
        onSearchQueryChanged = { onIntent(InvestmentsIntent.SearchQueryChanged(it)) },
        onBack = onBack,
        withTopAppBarActions = uiState.items.isNotEmpty(),
        topAppBarActionsContent = {
            val filterMenuItems = uiState.filteringUiInfo.filterMenuItems

            if (filterMenuItems.isNotEmpty()) {
                FilterMenu(
                    items = uiState.filteringUiInfo.filterMenuItems,
                    onFilter = { item -> onIntent(InvestmentsIntent.SetFiltering(item)) },
                )
            }
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
                    uiState.headerItems,
                    onHeaderItemClick = { onIntent(InvestmentsIntent.HeaderItemClick(it)) },
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
    headerItems: List<InvestmentsHeaderItem>,
    onHeaderItemClick: (InvestmentsHeaderItem) -> Unit,

    investments: List<Investment>,
    onInvestmentClick: (Investment) -> Unit,

    filteringUiInfo: FilteringUiInfo,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    val screenName = stringResource(R.string.investments)
    val configuration = LocalConfiguration.current
    val orientation = configuration.orientation

    Row(
        modifier = Modifier.fillMaxSize()
    ) {

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Header(
                items = headerItems,
                onItemClick = { onHeaderItemClick(it) },
                orientation
            )
        }

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .semantics { contentDescription = screenName },
            contentPadding = PaddingValues(
                horizontal = dimensionResource(R.dimen.horizontal_margin),
                vertical = dimensionResource(R.dimen.vertical_margin)
            ),
            state = listState
        ) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                item {
                    Header(
                        items = headerItems,
                        onItemClick = { onHeaderItemClick(it) },
                        orientation
                    )
                }
            }

            item {
                Spacer(
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.vertical_margin))
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

            items(investments) { investment ->
                InvestmentItem(
                    investment = investment,
                    onInvestmentClick = onInvestmentClick
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
                    text = investment.currency.toString(),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
@Preview
@LandscapePreview
private fun InvestmentsScreenScaffoldPreview() {
    UrgatTheme {
        InvestmentsScreenScaffold(
            uiState = InvestmentsUiState(
                headerItems = listOf(
                    HeaderItem(
                        InvestmentsHeaderItemId.Investment,
                        Icons.Filled.AttachMoney,
                        R.string.add_investment
                    ),
                ),
                items = SampleData.SAMPLE_INVESTMENTS
            ),
            onIntent = {},
            onInvestmentClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}

@Composable
@Preview
@LandscapePreview
private fun InvestmentsScreenScaffoldLoadingPreview() {
    UrgatTheme {
        InvestmentsScreenScaffold(
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

@Composable
@Preview
@LandscapePreview
private fun InvestmentsScreenScaffoldErrorPreview() {
    UrgatTheme {
        InvestmentsScreenScaffold(
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

@Composable
@Preview
@LandscapePreview
private fun InvestmentsContentPreview() {
    UrgatTheme {
        InvestmentsContent(
            headerItems = listOf(
                HeaderItem(
                    InvestmentsHeaderItemId.Investment,
                    Icons.Filled.AttachMoney,
                    R.string.add_investment
                ),
            ),
            onHeaderItemClick = {},
            investments = SampleData.SAMPLE_INVESTMENTS,
            onInvestmentClick = {},
            filteringUiInfo = InvestmentsFilteringUiInfo(),
            listState = rememberLazyListState(),
            modifier = Modifier
        )
    }
}
