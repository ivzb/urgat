package com.ivzb.urgat.ui.fuelConsumption

sealed class FuelConsumptionIntent {
    data class SearchQueryChanged(val query: String): FuelConsumptionIntent()
    data class SetFiltering(val filterType: String): FuelConsumptionIntent()
    data class SetSorting(val sortType: FuelConsumptionSortType): FuelConsumptionIntent()
    object ResetFilters: FuelConsumptionIntent()
}
