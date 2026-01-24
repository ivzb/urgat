package com.ivzb.urgat.ui.addEditSaving

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator

fun EntryProviderScope<NavKey>.addEditSavingEntry(navigator: Navigator) {
    entry<AddEditSavingRoute> { route ->
        val viewModel = hiltViewModel<AddEditSavingViewModel, AddEditSavingViewModel.Factory>(
            creationCallback = { factory ->
                factory.create(route)
            }
        )

        AddEditSavingScreen(
            viewModel = viewModel,

            onBack = { navigator.goBack() },
        )
    }
}