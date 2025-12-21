package com.ivzb.urgat.ui.sample

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator

fun EntryProviderScope<NavKey>.sampleEntry(navigator: Navigator) {
    entry<SampleRoute> {
        SampleScreen(
            viewModel = hiltViewModel(),

            onBack = { navigator.goBack() }
        )
    }
}