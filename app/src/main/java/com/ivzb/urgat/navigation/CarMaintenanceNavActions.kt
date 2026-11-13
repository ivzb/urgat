package com.ivzb.urgat.navigation

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.addEditCarMaintenance.AddEditCarMaintenanceRoute

fun UrgatNavActions.navigateToAddCarMaintenance() {
    navigator.navigate(
        AddEditCarMaintenanceRoute(
            id = null,
            titleResId = R.string.add_car_maintenance,
        )
    )
}

fun UrgatNavActions.navigateToEditCarMaintenance(carMaintenanceId: String) {
    navigator.navigate(
        AddEditCarMaintenanceRoute(
            id = carMaintenanceId,
            titleResId = R.string.edit_car_maintenance,
        )
    )
}

fun UrgatNavActions.navigateToCarMaintenance() {
    // todo
}
