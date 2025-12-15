package com.ivzb.urgat.ui.addEditExpense

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator

fun EntryProviderScope<NavKey>.addEditExpenseEntry(navigator: Navigator) {
    entry<AddEditExpenseRoute> { route ->
        val viewModel = hiltViewModel<AddEditExpenseViewModel, AddEditExpenseViewModel.Factory>(
            creationCallback = { factory ->
                factory.create(route)
            }
        )

        AddEditExpenseScreen(
            viewModel = viewModel,

            onBack = { navigator.goBack() },
        )
    }
}