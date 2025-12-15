package com.ivzb.urgat.ui.trackWeight

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator

fun EntryProviderScope<NavKey>.trackWeightEntry(navigator: Navigator) {
    entry<TrackWeightRoute> {
        TrackWeightScreen(
            viewModel = hiltViewModel(),

            onBack = { navigator.goBack() },
        )
    }
}