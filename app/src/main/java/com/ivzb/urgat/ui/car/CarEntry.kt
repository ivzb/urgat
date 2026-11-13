package com.ivzb.urgat.ui.car

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator
import com.ivzb.urgat.navigation.UrgatNavActions
import com.ivzb.urgat.navigation.navigateToAddCarMaintenance
import com.ivzb.urgat.navigation.navigateToAddFuelConsumption
import com.ivzb.urgat.navigation.navigateToCarMaintenance
import com.ivzb.urgat.navigation.navigateToEditCarMaintenance
import com.ivzb.urgat.navigation.navigateToEditFuelConsumption
import com.ivzb.urgat.navigation.navigateToFuelConsumption

fun EntryProviderScope<NavKey>.carEntry(navigator: Navigator, navActions: UrgatNavActions) {
    entry<CarRoute> {
        CarScreen(
            viewModel = hiltViewModel(),

            onAddFuelConsumption = { navActions.navigateToAddFuelConsumption() },
            onMoreFuelConsumption = { navActions.navigateToFuelConsumption() },
            onFuelConsumptionClick = { navActions.navigateToEditFuelConsumption(it.id) },

            onAddCarMaintenance = { navActions.navigateToAddCarMaintenance() },
            onMoreCarMaintenance = { navActions.navigateToCarMaintenance() },
            onCarMaintenanceClick = { navActions.navigateToEditCarMaintenance(it.id) },

            navigator = navigator,
        )
    }
}
