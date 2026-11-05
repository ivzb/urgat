package com.ivzb.urgat.domain.carMaintenance

import com.ivzb.urgat.data.carMaintenance.CarMaintenance
import com.ivzb.urgat.ui.carMaintenance.CAR_MAINTENANCE_FILTER_TYPE_ALL

fun filterCarMaintenance(items: List<CarMaintenance>, filterType: String): List<CarMaintenance> {
    if (filterType == CAR_MAINTENANCE_FILTER_TYPE_ALL) {
        return items
    }

    return items.filter { it.carName == filterType }
}
