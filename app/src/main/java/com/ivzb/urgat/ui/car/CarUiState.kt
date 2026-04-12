package com.ivzb.urgat.ui.car

import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.components.uiState.PagerUiState
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionUiState

data class CarUiState(
    val fuelConsumptionUiState: FuelConsumptionUiState = FuelConsumptionUiState(),

    override val isLoading: Boolean = false,
    override val hasError: Boolean = false,
    override val isEmpty: Boolean = false,

    override val pages: List<Page> = CAR_PAGES,
    override val initialPage: Int = 0,

    override val filteringUiInfo: CarFilteringUiInfo = CarFilteringUiInfo(),

    val uiEvent: CarUiEvent? = null,
): PagerUiState {

    val hasAppliedFilters: Boolean = fuelConsumptionUiState.filteringUiInfo.hasAppliedFilters

    fun withTopbarActions(page: Page): Boolean {
        val uiState = when (page) {
            CarPage.FuelConsumption -> fuelConsumptionUiState
            else -> null
        }

        return uiState?.let { !it.isEmpty || it.filteringUiInfo.hasAppliedFilters } ?: false
    }

}
