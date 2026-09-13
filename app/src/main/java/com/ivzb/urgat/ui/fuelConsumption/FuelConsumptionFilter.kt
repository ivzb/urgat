package com.ivzb.urgat.ui.fuelConsumption

/**
 * The fuel consumption filter state.
 *
 * Fuel type and car are independent dimensions and are applied together (AND),
 * each driven by its own menu in the top app bar.
 */
data class FuelConsumptionFilter(
    val type: String = FuelConsumptionFilterType_All,
    val carName: String = FuelConsumptionFilterType_All,
) {

    val hasType: Boolean = type != FuelConsumptionFilterType_All

    val hasCar: Boolean = carName != FuelConsumptionFilterType_All

    val hasAppliedFilters: Boolean = hasType || hasCar

    companion object {

        val None = FuelConsumptionFilter()

    }
}

