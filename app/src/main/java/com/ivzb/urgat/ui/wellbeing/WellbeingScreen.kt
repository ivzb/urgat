package com.ivzb.urgat.ui.wellbeing

import android.content.res.Configuration
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.data.weight.Weight
import com.ivzb.urgat.navigation.FakeNavigator
import com.ivzb.urgat.navigation.Navigator
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.EmptyStateCard
import com.ivzb.urgat.ui.components.EmptyStateScreen
import com.ivzb.urgat.ui.components.ItemHeaderWithSeeAll
import com.ivzb.urgat.ui.components.LoadingState
import com.ivzb.urgat.ui.components.NavigationScaffold
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.ui.weights.WeightItem
import com.ivzb.urgat.ui.weights.WeightsChart
import com.ivzb.urgat.ui.weights.WeightsFilteringUiInfo

@Composable
fun WellbeingScreen(
    viewModel: WellbeingViewModel,

    onTrackWeight: () -> Unit,
    onSeeAllWeights: () -> Unit,
    onWeightClick: (Weight) -> Unit,

    navigator: Navigator,
    modifier: Modifier = Modifier,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WellbeingScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,

        onSeeAllWeights = onSeeAllWeights,
        onWeightClick = onWeightClick,

        navigator = navigator,
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

@VisibleForTesting
@Composable
fun WellbeingScreen(
    uiState: WellbeingUiState,
    onIntent: (WellbeingIntent) -> Unit,

    onSeeAllWeights: () -> Unit,
    onWeightClick: (Weight) -> Unit,

    navigator: Navigator,
    modifier: Modifier = Modifier,
) {

    NavigationScaffold(
        selectedRoute = WellbeingRoute,
        onSearchQueryChanged = { /* NOOP */ },
        fabActions = uiState.fabActions,
        onFabActionClick = { id -> onIntent(WellbeingIntent.FabActionClick(id)) },
        withTopAppBarActions = false,
        navigator = navigator,
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

            else -> {
                WellbeingContent(
                    weights = uiState.weights,
                    weightsFilteringUiInfo = uiState.weightsFilteringUiInfo,
                    onTrackWeight = { onIntent(WellbeingIntent.FabActionClick(WellbeingFabActionId.Weight)) },
                    onSeeAllWeights = onSeeAllWeights,
                    onWeightClick = onWeightClick,
                )
            }
        }
    }
}

@Composable
private fun WellbeingContent(
    weights: List<Weight>,
    weightsFilteringUiInfo: WeightsFilteringUiInfo,

    onTrackWeight: () -> Unit,
    onSeeAllWeights: () -> Unit,
    onWeightClick: (Weight) -> Unit,

    modifier: Modifier = Modifier
) {

    val configuration = LocalConfiguration.current

    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        WellbeingContentLandscape(
            weights = weights,
            weightsFilteringUiInfo = weightsFilteringUiInfo,
            onTrackWeight = onTrackWeight,
            onSeeAllWeights = onSeeAllWeights,
            onWeightClick = onWeightClick,
            modifier = modifier,
        )
    } else {
        WellbeingContentPortrait(
            weights = weights,
            weightsFilteringUiInfo = weightsFilteringUiInfo,
            onTrackWeight = onTrackWeight,
            onSeeAllWeights = onSeeAllWeights,
            onWeightClick = onWeightClick,
            modifier = modifier,
        )
    }
}

@Composable
private fun WellbeingContentPortrait(
    weights: List<Weight>,
    weightsFilteringUiInfo: WeightsFilteringUiInfo,

    onTrackWeight: () -> Unit,
    onSeeAllWeights: () -> Unit,
    onWeightClick: (Weight) -> Unit,

    modifier: Modifier = Modifier
) {
    val screenName = stringResource(R.string.wellbeing)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = screenName },
        contentPadding = PaddingValues(
            horizontal = dimensionResource(R.dimen.content_padding_horizontal),
            vertical = dimensionResource(R.dimen.content_padding_vertical),
        ),
    ) {

        itemWeights(
            weights = weights,
            weightsFilteringUiInfo = weightsFilteringUiInfo,
            onTrackWeight = onTrackWeight,
            onSeeAllWeights = onSeeAllWeights,
            onWeightClick = onWeightClick,
        )

        item {
            Spacer(
                modifier = Modifier
                    .height(dimensionResource(R.dimen.vertical_margin))
            )
        }
    }
}

@Composable
private fun WellbeingContentLandscape(
    weights: List<Weight>,
    weightsFilteringUiInfo: WeightsFilteringUiInfo,

    onTrackWeight: () -> Unit,
    onSeeAllWeights: () -> Unit,
    onWeightClick: (Weight) -> Unit,

    modifier: Modifier = Modifier
) {
    val screenName = stringResource(R.string.wellbeing)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.content_padding_horizontal)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {

        LazyColumn(
            modifier = modifier
                .weight(1f)
                .semantics { contentDescription = screenName },
            contentPadding = PaddingValues(
                horizontal = dimensionResource(R.dimen.content_padding_horizontal),
                vertical = dimensionResource(R.dimen.content_padding_vertical),
            ),
        ) {

            itemWeights(
                weights = weights,
                weightsFilteringUiInfo = weightsFilteringUiInfo,
                onTrackWeight = onTrackWeight,
                onSeeAllWeights = onSeeAllWeights,
                onWeightClick = onWeightClick,
            )

            item {
                Spacer(
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.vertical_margin))
                )
            }
        }

        LazyColumn(
            modifier = modifier
                .weight(1f)
                .semantics { contentDescription = screenName },
            contentPadding = PaddingValues(
                horizontal = dimensionResource(R.dimen.content_padding_horizontal),
                vertical = dimensionResource(R.dimen.content_padding_vertical),
            ),
        ) {

            // nothing here yet
        }
    }
}

private fun LazyListScope.itemWeights(
    weights: List<Weight>,
    weightsFilteringUiInfo: WeightsFilteringUiInfo,
    onTrackWeight: () -> Unit,
    onSeeAllWeights: () -> Unit,
    onWeightClick: (Weight) -> Unit,
) {
    when {
        weights.isEmpty() -> {
            item {
                EmptyStateCard(
                    iconRes = weightsFilteringUiInfo.emptyIconRes,
                    label = weightsFilteringUiInfo.emptyLabel,
                    message = weightsFilteringUiInfo.emptyMessage,
                    buttonLabel = weightsFilteringUiInfo.emptyButtonLabel,
                    onButtonClick = onTrackWeight,
                )
            }
        }

        else -> {
            item {
                ItemHeaderWithSeeAll(
                    title = stringResource(weightsFilteringUiInfo.currentFilteringLabel),
                    onSeeAllClick = onSeeAllWeights,
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

            items(
                items = weights,
                key = { weight -> weight.id },
            ) { weight ->

                WeightItem(
                    weight = weight,
                    onWeightClick = onWeightClick,
                )
            }
        }
    }
}

@AllDevicesPreview
@Composable
private fun WellbeingScreenPreview() {
    UrgatTheme {
        WellbeingScreen(
            uiState = WellbeingUiState(
                weights = SampleData.SAMPLE_WEIGHTS,
                weightsFilteringUiInfo = WeightsFilteringUiInfo(),
                isLoading = false,
                hasError = false,
                filteringUiInfo = WellbeingFilteringUiInfo(),
            ),
            onIntent = {},
            onSeeAllWeights = {},
            onWeightClick = {},
            navigator = FakeNavigator(),
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun WellbeingScreenEmptyPreview() {
    UrgatTheme {
        WellbeingScreen(
            uiState = WellbeingUiState(
                weights = listOf(),
                weightsFilteringUiInfo = WeightsFilteringUiInfo(),
                isLoading = false,
                hasError = false,
                filteringUiInfo = WellbeingFilteringUiInfo(),
            ),
            onIntent = {},
            onSeeAllWeights = {},
            onWeightClick = {},
            navigator = FakeNavigator(),
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun WellbeingScreenLoadingPreview() {
    UrgatTheme {
        WellbeingScreen(
            uiState = WellbeingUiState(
                weights = listOf(),
                weightsFilteringUiInfo = WeightsFilteringUiInfo(),
                isLoading = true,
                hasError = false,
                filteringUiInfo = WellbeingFilteringUiInfo(),
            ),
            onIntent = {},
            onSeeAllWeights = {},
            onWeightClick = {},
            navigator = FakeNavigator(),
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun WellbeingScreenErrorPreview() {
    UrgatTheme {
        WellbeingScreen(
            uiState = WellbeingUiState(
                weights = listOf(),
                weightsFilteringUiInfo = WeightsFilteringUiInfo(),
                isLoading = false,
                hasError = true,
                filteringUiInfo = WellbeingFilteringUiInfo(),
            ),
            onIntent = {},
            onSeeAllWeights = {},
            onWeightClick = {},
            navigator = FakeNavigator(),
            modifier = Modifier
        )
    }
}