package com.ivzb.urgat.ui.car

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.fuelConsumption.FuelConsumption
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.navigation.FakeNavigator
import com.ivzb.urgat.navigation.Navigator
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.EmptyState
import com.ivzb.urgat.ui.components.FilterMenu
import com.ivzb.urgat.ui.components.ItemHeaderWithMore
import com.ivzb.urgat.ui.components.MoreMenu
import com.ivzb.urgat.ui.components.SortMenu
import com.ivzb.urgat.ui.components.screens.NavigationPagerScreen
import com.ivzb.urgat.ui.components.uiState.UiState
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionFilteringUiInfo
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionUiState
import com.ivzb.urgat.ui.theme.UrgatTheme
import java.math.BigDecimal

@Composable
fun CarScreen(
    viewModel: CarViewModel,

    onAddFuelConsumption: () -> Unit,
    onMoreFuelConsumption: () -> Unit,
    onFuelConsumptionClick: (FuelConsumption) -> Unit,

    navigator: Navigator,
    modifier: Modifier = Modifier,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CarScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,

        onMoreFuelConsumption = onMoreFuelConsumption,
        onFuelConsumptionClick = onFuelConsumptionClick,

        navigator = navigator,
        modifier = modifier
    )

    LaunchedEffect(uiState.uiEvent) {
        uiState.uiEvent?.let { event ->
            when (event) {
                is CarUiEvent.AddFuelConsumption -> onAddFuelConsumption()
            }

            viewModel.onIntent(CarIntent.EventConsumed)
        }
    }
}

@VisibleForTesting
@Composable
fun CarScreen(
    uiState: CarUiState,
    onIntent: (CarIntent) -> Unit,

    onMoreFuelConsumption: () -> Unit,
    onFuelConsumptionClick: (FuelConsumption) -> Unit,

    navigator: Navigator,
    modifier: Modifier = Modifier,
) {

    var isSearchFocused by remember { mutableStateOf(false) }

    NavigationPagerScreen(
        uiState = uiState,

        route = CarRoute,
        searchQuery = uiState.filteringUiInfo.searchQuery,
        onSearchQueryChanged = { query -> onIntent(CarIntent.SearchQueryChanged(query)) },
        onFabActionClick = { page -> onIntent(CarIntent.FabActionClick(page)) },
        navigator = navigator,

        withSearch = { page -> uiState.withTopbarActions(page) },
        withFab = { page -> !isSearchFocused },
        onSearchFocusChanged = { isSearchFocused = it },
        topAppBarActionsContent = { page ->
            if (uiState.withTopbarActions(page)) {
                when (page) {
                    CarPage.FuelConsumption -> {
                        FilterMenu(
                            items = uiState.fuelConsumptionUiState.filteringUiInfo.filterMenuItems,
                            showBadge = uiState.fuelConsumptionUiState.filteringUiInfo.showFilterBadge,
                            onFilter = { onIntent(CarIntent.SetFuelConsumptionFiltering(it)) },
                        )

                        SortMenu(
                            items = uiState.fuelConsumptionUiState.filteringUiInfo.sortMenuItems,
                            showBadge = uiState.fuelConsumptionUiState.filteringUiInfo.showSortBadge,
                            onFilter = { onIntent(CarIntent.SetFuelConsumptionSorting(it)) },
                        )
                    }
                }

                MoreMenu(
                    listOfNotNull(
                        (R.string.reset_filters to { onIntent(CarIntent.ResetFilters) }).takeIf { uiState.hasAppliedFilters },
                    )
                )
            }
        },

        content = { page, listState, onListStateChanged ->
            when (page) {
                CarPage.FuelConsumption -> FuelConsumptionPage(
                    uiState = uiState.fuelConsumptionUiState,

                    onMoreFuelConsumption = onMoreFuelConsumption,
                    onAddFuelConsumption = { onIntent(CarIntent.FabActionClick(page)) },
                    onFuelConsumptionClick = onFuelConsumptionClick,
                    onResetFilters = { onIntent(CarIntent.ResetFilters) },

                    listState = listState,
                    onListStateChanged = onListStateChanged,
                )
            }
        },

        modifier = modifier
    )
}

@Composable
private fun CarPage(
    uiState: UiState,
    onEmptyStateButtonClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    when {
        uiState.isEmpty -> {
            EmptyState(uiState.filteringUiInfo, onEmptyStateButtonClick)
        }

        else -> {
            content()
        }
    }
}

@Composable
private fun FuelConsumptionPage(
    uiState: FuelConsumptionUiState,

    onMoreFuelConsumption: () -> Unit,
    onAddFuelConsumption: () -> Unit,
    onFuelConsumptionClick: (FuelConsumption) -> Unit,
    onResetFilters: () -> Unit,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    CarPage(
        uiState = uiState,
        onEmptyStateButtonClick = if (uiState.filteringUiInfo.hasAppliedFilters) onResetFilters else onAddFuelConsumption,

        content = {
            com.ivzb.urgat.ui.fuelConsumption.TransactionsPage(
                fuelConsumption = uiState.fuelConsumption,
                onFuelConsumptionClick = onFuelConsumptionClick,

                header = {
                    ItemHeaderWithMore(
                        title = stringResource(uiState.filteringUiInfo.currentFilteringLabel),
                        onMoreClick = onMoreFuelConsumption,
                    )
                },

                listState = listState,
                onListStateChanged = onListStateChanged,
            )
        }
    )
}

@AllDevicesPreview
@Composable
private fun CarScreenPreview() {
    UrgatTheme {
        CarScreen(
            uiState = CarUiState(
                fuelConsumptionUiState = FuelConsumptionUiState(
                    fuelConsumption = SampleData.SAMPLE_FUEL_CONSUMPTION_GROUPED,
                    filteringUiInfo = FuelConsumptionFilteringUiInfo(),
                ),
                isLoading = false,
                hasError = false,
                filteringUiInfo = CarFilteringUiInfo(),
            ),
            onIntent = {},
            onMoreFuelConsumption = {},
            onFuelConsumptionClick = {},
            navigator = FakeNavigator(),
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun CarScreenEmptyPreview() {
    UrgatTheme {
        CarScreen(
            uiState = CarUiState(
                fuelConsumptionUiState = FuelConsumptionUiState(
                    fuelConsumption = listOf(),
                    filteringUiInfo = FuelConsumptionFilteringUiInfo(),
                ),
                isLoading = false,
                hasError = false,
                filteringUiInfo = CarFilteringUiInfo(),
            ),
            onIntent = {},
            onMoreFuelConsumption = {},
            onFuelConsumptionClick = {},
            navigator = FakeNavigator(),
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun CarScreenLoadingPreview() {
    UrgatTheme {
        CarScreen(
            uiState = CarUiState(
                fuelConsumptionUiState = FuelConsumptionUiState(
                    fuelConsumption = listOf(),
                    filteringUiInfo = FuelConsumptionFilteringUiInfo(),
                ),
                isLoading = true,
                hasError = false,
                filteringUiInfo = CarFilteringUiInfo(),
            ),
            onIntent = {},
            onMoreFuelConsumption = {},
            onFuelConsumptionClick = {},
            navigator = FakeNavigator(),
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun CarScreenErrorPreview() {
    UrgatTheme {
        CarScreen(
            uiState = CarUiState(
                fuelConsumptionUiState = FuelConsumptionUiState(
                    fuelConsumption = listOf(),
                    filteringUiInfo = FuelConsumptionFilteringUiInfo(),
                ),
                isLoading = false,
                hasError = true,
                filteringUiInfo = CarFilteringUiInfo(),
            ),
            onIntent = {},
            onMoreFuelConsumption = {},
            onFuelConsumptionClick = {},
            navigator = FakeNavigator(),
            modifier = Modifier
        )
    }
}
