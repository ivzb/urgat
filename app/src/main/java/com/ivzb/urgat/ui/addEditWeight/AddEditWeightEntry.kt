package com.ivzb.urgat.ui.addEditWeight

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator

fun EntryProviderScope<NavKey>.addEditWeightEntry(navigator: Navigator) {
    entry<AddEditWeightRoute> { route ->
        val viewModel = hiltViewModel<AddEditWeightViewModel, AddEditWeightViewModel.Factory>(
            creationCallback = { factory ->
                factory.create(route)
            }
        )
        
        AddEditWeightScreen(
            viewModel = viewModel,

            onBack = { navigator.goBack() },
        )
    }
}