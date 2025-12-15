package com.ivzb.urgat.ui.addEditInvestment

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator

fun EntryProviderScope<NavKey>.addEditInvestmentEntry(navigator: Navigator) {
    entry<AddEditInvestmentRoute> { route ->
        val viewModel = hiltViewModel<AddEditInvestmentViewModel, AddEditInvestmentViewModel.Factory>(
            creationCallback = { factory ->
                factory.create(route)
            }
        )

        AddEditInvestmentScreen(
            viewModel = viewModel,

            onBack = { navigator.goBack() },
        )
    }
}