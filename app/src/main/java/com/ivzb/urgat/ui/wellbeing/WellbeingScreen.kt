package com.ivzb.urgat.ui.wellbeing

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ivzb.urgat.R
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.data.weight.Weight
import com.ivzb.urgat.navigation.WellbeingDestination
import com.ivzb.urgat.ui.components.EmptyStateScreen
import com.ivzb.urgat.ui.components.Header
import com.ivzb.urgat.ui.components.HeaderItem
import com.ivzb.urgat.ui.components.LandscapePreview
import com.ivzb.urgat.ui.components.LoadingState
import com.ivzb.urgat.ui.components.MenuScaffold
import com.ivzb.urgat.ui.components.charts.LineChart
import com.ivzb.urgat.ui.components.charts.LineChartItem
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun WellbeingScreen(
    onTrackWeight: () -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: WellbeingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WellbeingScreenScaffold(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        navController = navController,
        modifier = modifier
    )

    LaunchedEffect(uiState.uiEvent) {
        uiState.uiEvent?.let { event ->
            when (event) {
                is WellbeingUiEvent.TrackWeight -> onTrackWeight()
            }

            viewModel.onIntent(WellbeingIntent.EventConsumed)
        }
    }
}

@Composable
private fun WellbeingScreenScaffold(
    uiState: WellbeingUiState,
    onIntent: (WellbeingIntent) -> Unit,

    navController: NavHostController,
    modifier: Modifier = Modifier,
) {

    MenuScaffold(
        selectedDestination = WellbeingDestination,
        onSearchQueryChanged = { onIntent(WellbeingIntent.SearchQueryChanged(it)) },
        navController = navController,
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
                WellbeingContent(
                    uiState.headerItems,
                    onHeaderItemClick = { onIntent(WellbeingIntent.HeaderItemClick(it)) },
                    weights = uiState.weights,
                    filteringUiInfo = uiState.filteringUiInfo,
                    listState = listState,
                )
            }
        }
    }
}

@Composable
private fun WellbeingContent(
    headerItems: List<WellbeingHeaderItem>,
    onHeaderItemClick: (WellbeingHeaderItem) -> Unit,
    weights: List<Weight>,
    filteringUiInfo: WellbeingFilteringUiInfo,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    val screenName = stringResource(R.string.wellbeing)
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

            itemWeightsChart(
                items = weights,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )

            if (weights.isEmpty()) {
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
}

private fun LazyListScope.itemWeightsChart(
    items: List<Weight>,
    modifier: Modifier = Modifier
) {
    item {
        LineChart(
            items = items.map { LineChartItem(it.value.toFloat(), it.date) },
            modifier = modifier
        )
    }
}



@Composable
@Preview
@LandscapePreview
private fun WellbeingScreenScaffoldPreview() {
    UrgatTheme {
        WellbeingScreenScaffold(
            uiState = WellbeingUiState(
                headerItems = listOf(
                    HeaderItem(
                        WellbeingHeaderItemId.Weight,
                        Icons.Filled.FitnessCenter,
                        R.string.track_weight
                    ),
                ),
                weights = SampleData.SAMPLE_WEIGHTS,
            ),
            onIntent = {},
            navController = rememberNavController(),
            modifier = Modifier
        )
    }
}

@Composable
@Preview
@LandscapePreview
private fun WellbeingScreenScaffoldLoadingPreview() {
    UrgatTheme {
        WellbeingScreenScaffold(
            uiState = WellbeingUiState(
                isLoading = true,
            ),
            onIntent = {},
            navController = rememberNavController(),
            modifier = Modifier
        )
    }
}

@Composable
@Preview
@LandscapePreview
private fun WellbeingScreenScaffoldErrorPreview() {
    UrgatTheme {
        WellbeingScreenScaffold(
            uiState = WellbeingUiState(
                hasError = true,
            ),
            onIntent = {},
            navController = rememberNavController(),
            modifier = Modifier
        )
    }
}

@Composable
@Preview
@LandscapePreview
private fun WellbeingContentPreview() {
    UrgatTheme {
        WellbeingContent(
            headerItems = listOf(
                HeaderItem(
                    WellbeingHeaderItemId.Weight,
                    Icons.Filled.FitnessCenter,
                    R.string.track_weight
                ),
            ),
            onHeaderItemClick = {},
            weights = SampleData.SAMPLE_WEIGHTS,
            filteringUiInfo = WellbeingFilteringUiInfo(),
            listState = rememberLazyListState(),
            modifier = Modifier
        )
    }
}