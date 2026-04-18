package com.ivzb.urgat.domain.carMaintenance

import com.ivzb.urgat.data.carMaintenance.CarMaintenance
import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceFilterType_All

fun filterCarMaintenance(items: List<CarMaintenance>, filterType: String): List<CarMaintenance> {
    if (filterType == CarMaintenanceFilterType_All) {
        return items
    }

    return items.filter { it.carName == filterType }
}

