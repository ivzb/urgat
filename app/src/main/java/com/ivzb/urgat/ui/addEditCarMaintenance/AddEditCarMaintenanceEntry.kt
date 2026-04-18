package com.ivzb.urgat.ui.addEditCarMaintenance

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator

fun EntryProviderScope<NavKey>.addEditCarMaintenanceEntry(navigator: Navigator) {
    entry<AddEditCarMaintenanceRoute> { route ->
        val viewModel = hiltViewModel<AddEditCarMaintenanceViewModel, AddEditCarMaintenanceViewModel.Factory>(
            creationCallback = { factory ->
                factory.create(route)
            }
        )

        AddEditCarMaintenanceScreen(
            viewModel = viewModel,

            onBack = { navigator.goBack() },
        )
    }
}

