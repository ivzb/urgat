package com.ivzb.urgat.ui.addEditFuelConsumption

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator

fun EntryProviderScope<NavKey>.addEditFuelConsumptionEntry(navigator: Navigator) {
    entry<AddEditFuelConsumptionRoute> { route ->
        val viewModel = hiltViewModel<AddEditFuelConsumptionViewModel, AddEditFuelConsumptionViewModel.Factory>(
            creationCallback = { factory ->
                factory.create(route)
            }
        )

        AddEditFuelConsumptionScreen(
            viewModel = viewModel,

            onBack = { navigator.goBack() },
        )
    }
}

