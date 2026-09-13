package com.ivzb.urgat.ui.car

import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceSortType
import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionSortType

sealed class CarIntent {
    data class SearchQueryChanged(val query: String): CarIntent()
    data class FabActionClick(val page: Page): CarIntent()

    data class SetFuelConsumptionFiltering(val filterType: String): CarIntent()
    data class SetFuelConsumptionCarFiltering(val carName: String): CarIntent()
    data class SetFuelConsumptionSorting(val sortType: FuelConsumptionSortType): CarIntent()

    data class SetCarMaintenanceFiltering(val filterType: String): CarIntent()
    data class SetCarMaintenanceSorting(val sortType: CarMaintenanceSortType): CarIntent()

    object ResetFilters: CarIntent()
    object EventConsumed: CarIntent()
}
