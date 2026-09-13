package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.data.fuelConsumption.FuelConsumption
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionFilter

fun filterFuelConsumption(items: List<FuelConsumption>, filter: FuelConsumptionFilter): List<FuelConsumption> {
    if (!filter.hasAppliedFilters) {
        return items
    }

    return items.filter { item ->
        (!filter.hasType || item.type == filter.type) &&
        (!filter.hasCar || item.carName == filter.carName)
    }
}