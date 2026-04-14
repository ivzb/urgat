package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.data.fuelConsumption.FuelConsumption
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionFilterType_All

fun filterFuelConsumption(items: List<FuelConsumption>, filterType: String): List<FuelConsumption> {
    if (filterType == FuelConsumptionFilterType_All) {
        return items
    }

    return items.filter { it.type == filterType }
}