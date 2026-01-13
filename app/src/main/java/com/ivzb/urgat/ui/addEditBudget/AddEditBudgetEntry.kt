package com.ivzb.urgat.ui.addEditBudget

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator

fun EntryProviderScope<NavKey>.addEditBudgetEntry(navigator: Navigator) {
    entry<AddEditBudgetRoute> { route ->
        val viewModel = hiltViewModel<AddEditBudgetViewModel, AddEditBudgetViewModel.Factory>(
            creationCallback = { factory ->
                factory.create(route)
            }
        )

        AddEditBudgetScreen(
            viewModel = viewModel,

            onBack = { navigator.goBack() },
        )
    }
}