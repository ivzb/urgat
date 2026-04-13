package com.ivzb.urgat.ui.fuelConsumption

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator
import com.ivzb.urgat.navigation.UrgatNavActions

fun EntryProviderScope<NavKey>.fuelConsumptionEntry(navigator: Navigator, navActions: UrgatNavActions) {
    entry<FuelConsumptionRoute> {
        FuelConsumptionScreen(
            viewModel = hiltViewModel(),

            onFuelConsumptionClick = { navActions.navigateToEditFuelConsumption(it.id) },
            onBack = { navigator.goBack() },
        )
    }
}