package com.ivzb.urgat.ui.weights

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator
import com.ivzb.urgat.navigation.UrgatNavActions

fun EntryProviderScope<NavKey>.weightsEntry(navigator: Navigator, navActions: UrgatNavActions) {
    entry<WeightsRoute> {
        WeightsScreen(
            viewModel = hiltViewModel(),

            onWeightClick = { navActions.navigateToEditWeight(it.id) },
            onBack = { navigator.goBack() },
        )
    }
}