package com.ivzb.urgat.ui.fuelConsumption

/**
 * The fuel consumption filter state.
 *
 * Fuel type and car are independent dimensions and are applied together (AND),
 * each driven by its own menu in the top app bar.
 */
data class FuelConsumptionFilter(
    val type: String = FUEL_CONSUMPTION_FILTER_TYPE_ALL,
    val carName: String = FUEL_CONSUMPTION_FILTER_TYPE_ALL,
) {

    val hasType: Boolean = type != FUEL_CONSUMPTION_FILTER_TYPE_ALL

    val hasCar: Boolean = carName != FUEL_CONSUMPTION_FILTER_TYPE_ALL

    val hasAppliedFilters: Boolean = hasType || hasCar

    companion object {

        val None = FuelConsumptionFilter()

    }
}
