package com.ivzb.urgat.navigation

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.addEditFuelConsumption.AddEditFuelConsumptionRoute
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionRoute

fun UrgatNavActions.navigateToAddFuelConsumption() {
    navigator.navigate(
        AddEditFuelConsumptionRoute(
            id = null,
            titleResId = R.string.add_fuel_consumption,
        )
    )
}

fun UrgatNavActions.navigateToEditFuelConsumption(fuelConsumptionId: String) {
    navigator.navigate(
        AddEditFuelConsumptionRoute(
            id = fuelConsumptionId,
            titleResId = R.string.edit_fuel_consumption,
        )
    )
}

fun UrgatNavActions.navigateToFuelConsumption() {
    navigator.navigate(FuelConsumptionRoute)
}
