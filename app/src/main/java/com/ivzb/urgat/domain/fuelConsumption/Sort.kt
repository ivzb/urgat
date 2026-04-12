package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.data.fuelConsumption.FuelConsumption
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionSortType

fun sortFuelConsumption(items: List<FuelConsumption>, sortType: FuelConsumptionSortType): List<FuelConsumption> {
    return when (sortType) {
        FuelConsumptionSortType.BY_DATE -> items.sortedByDescending { it.date }
        FuelConsumptionSortType.BY_MIN -> items.sortedBy { it.price }
        FuelConsumptionSortType.BY_MAX -> items.sortedByDescending { it.price }
    }
}

