package com.ivzb.urgat.domain.carMaintenance

import com.ivzb.urgat.data.carMaintenance.CarMaintenance
import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceSortType

fun sortCarMaintenance(items: List<CarMaintenance>, sortType: CarMaintenanceSortType): List<CarMaintenance> {
    return when (sortType) {
        CarMaintenanceSortType.BY_DATE -> items.sortedByDescending { it.date }
        CarMaintenanceSortType.BY_ODOMETER_MIN -> items.sortedBy { it.odometer }
        CarMaintenanceSortType.BY_ODOMETER_MAX -> items.sortedByDescending { it.odometer }
    }
}

