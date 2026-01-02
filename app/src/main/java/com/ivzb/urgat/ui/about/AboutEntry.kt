package com.ivzb.urgat.ui.about

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator

fun EntryProviderScope<NavKey>.aboutEntry(navigator: Navigator) {
    entry<AboutRoute> {
        AboutScreen(
            viewModel = hiltViewModel(),

            onBack = { navigator.goBack() }
        )
    }
}