package com.ivzb.urgat.ui.weights

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.data.weight.Weight
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.ItemHeaderLarge
import com.ivzb.urgat.ui.components.ListItem
import com.ivzb.urgat.ui.components.SortMenu
import com.ivzb.urgat.ui.components.charts.LineChart
import com.ivzb.urgat.ui.components.charts.LineChartItem
import com.ivzb.urgat.ui.components.screens.ListDetailScreen
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.util.format
import com.ivzb.urgat.util.toLocalDate

@Composable
fun WeightsScreen(
    viewModel: WeightsViewModel,

    onAddWeight: () -> Unit,
    onWeightClick: (Weight) -> Unit,
    onBack: () -> Unit,

    modifier: Modifier = Modifier,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WeightsScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onAddWeight = onAddWeight,
        onWeightClick = onWeightClick,
        onBack = onBack,
        modifier = modifier
    )
}

@VisibleForTesting
@Composable
fun WeightsScreen(
    uiState: WeightsUiState,
    onIntent: (WeightsIntent) -> Unit,
    onAddWeight: () -> Unit,
    onWeightClick: (Weight) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = remember { LazyListState() }

    ListDetailScreen(
        uiState = uiState,

        route = WeightsRoute,
        searchQuery = "",
        onSearchQueryChanged = { /* NOOP */ },
        onAddItem = onAddWeight,
        onBack = onBack,

        withSearch = false,
        topAppBarActionsContent = {
            SortMenu(
                items = uiState.filteringUiInfo.sortMenuItems,
                showBadge = uiState.filteringUiInfo.showSortBadge,
                onFilter = { onIntent(WeightsIntent.SetSorting(it)) },
            )
        },
        listState = listState,

        content = {
            WeightsContent(
                weights = uiState.items,
                onWeightClick = onWeightClick,
                filteringUiInfo = uiState.filteringUiInfo,
                listState = listState
            )
        },

        modifier = modifier,
    )
}

@Composable
fun WeightsContent(
    weights: List<Weight>,
    onWeightClick: (Weight) -> Unit,
    filteringUiInfo: WeightsFilteringUiInfo,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {

    LazyColumn (
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
            Spacer(modifier = Modifier
                .height(dimensionResource(R.dimen.vertical_margin))
            )
        }

        item {
            WeightsChart(
                items = weights,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.weights_chart_height))
            )
        }

        item {
            Spacer(modifier = Modifier
                .height(dimensionResource(R.dimen.vertical_margin))
            )
        }

        items(
            items = weights,
            key = { weight -> weight.id },
        ) { weight ->
            WeightItem(
                weight = weight,
                onWeightClick = onWeightClick,
            )
        }

        item {
            Spacer(modifier = Modifier
                .height(dimensionResource(R.dimen.vertical_margin))
            )
        }
    }
}

@Composable
fun WeightsChart(
    items: List<Weight>,
    modifier: Modifier = Modifier
) {

    LineChart(
        items = items.map { LineChartItem(it.value.toFloat(), it.date) },
        modifier = modifier
    )
}

@Composable
fun WeightItem(
    weight: Weight,
    onWeightClick: (Weight) -> Unit,
) {
    val date = remember { weight.date.toLocalDate() }

    ListItem(
        textLeft = date.dayOfMonth.toString(),
        subTextLeft = date.month.name.take(3),

        textRight = weight.value.format(),
        subTextRight = "",

        onClick = { onWeightClick(weight) }
    )
}

@AllDevicesPreview
@Composable
private fun WeightsScreenPreview() {
    UrgatTheme {
        WeightsScreen(
            uiState = WeightsUiState(
                items = SampleData.SAMPLE_WEIGHTS,
                filteringUiInfo = WeightsFilteringUiInfo(),
                isLoading = false,
                hasError = false
            ),
            onIntent = {},
            onAddWeight = {},
            onWeightClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun WeightsScreenEmptyPreview() {
    UrgatTheme {
        WeightsScreen(
            uiState = WeightsUiState(
                items = listOf(),
                filteringUiInfo = WeightsFilteringUiInfo(),
                isLoading = false,
                hasError = false
            ),
            onIntent = {},
            onAddWeight = {},
            onWeightClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun WeightsScreenLoadingPreview() {
    UrgatTheme {
        WeightsScreen(
            uiState = WeightsUiState(
                items = listOf(),
                filteringUiInfo = WeightsFilteringUiInfo(),
                isLoading = true,
                hasError = false
            ),
            onIntent = {},
            onAddWeight = {},
            onWeightClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun WeightsScreenErrorPreview() {
    UrgatTheme {
        WeightsScreen(
            uiState = WeightsUiState(
                items = listOf(),
                filteringUiInfo = WeightsFilteringUiInfo(),
                isLoading = false,
                hasError = true
            ),
            onIntent = {},
            onAddWeight = {},
            onWeightClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}
