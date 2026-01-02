package com.ivzb.urgat.ui.wellbeing

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator
import com.ivzb.urgat.navigation.UrgatNavActions

fun EntryProviderScope<NavKey>.wellbeingEntry(navigator: Navigator, navActions: UrgatNavActions) {
    entry<WellbeingRoute> {
        WellbeingScreen(
            viewModel = hiltViewModel(),

            onAddWeight = { navActions.navigateToAddWeight() },
            onSeeAllWeights = { navActions.navigateToWeights() },
            onWeightClick = { navActions.navigateToEditWeight(it.id) },

            navigator = navigator,
        )
    }
}