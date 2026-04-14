package com.ivzb.urgat.ui.car

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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

    NavigationPagerScreen(
        uiState = uiState,

        route = CarRoute,
        searchQuery = uiState.filteringUiInfo.searchQuery,
        onSearchQueryChanged = { query -> onIntent(CarIntent.SearchQueryChanged(query)) },
        onFabActionClick = { page -> onIntent(CarIntent.FabActionClick(page)) },
        navigator = navigator,

        withSearch = { page -> uiState.withTopbarActions(page) },
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

private val SAMPLE_FUEL_CONSUMPTION = listOf(
    FuelConsumption("fc_0", BigDecimal("45.5"), BigDecimal("2.35"), "EUR", "Diesel", "Mercedes w140", 150000, System.currentTimeMillis(), false, System.currentTimeMillis()),
    FuelConsumption("fc_1", BigDecimal("40.0"), BigDecimal("2.40"), "EUR", "Diesel", "Mercedes w140", 149500, System.currentTimeMillis() - 86_400_000, false, System.currentTimeMillis()),
    FuelConsumption("fc_2", BigDecimal("50.0"), BigDecimal("2.30"), "EUR", "Diesel", "Mercedes w140", 149000, System.currentTimeMillis() - 2 * 86_400_000, false, System.currentTimeMillis()),
    FuelConsumption("fc_3", BigDecimal("42.0"), BigDecimal("2.38"), "EUR", "Diesel", "Mercedes w140", 148500, System.currentTimeMillis() - 3 * 86_400_000, false, System.currentTimeMillis()),
    FuelConsumption("fc_4", BigDecimal("48.0"), BigDecimal("2.32"), "EUR", "Diesel", "Mercedes w140", 148000, System.currentTimeMillis() - 4 * 86_400_000, false, System.currentTimeMillis()),
)

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
