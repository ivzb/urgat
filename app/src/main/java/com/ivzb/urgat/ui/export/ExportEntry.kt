package com.ivzb.urgat.ui.export

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator

fun EntryProviderScope<NavKey>.exportEntry(navigator: Navigator) {
    entry<ExportRoute> {
        ExportScreen(
            viewModel = hiltViewModel(),

            onBack = { navigator.goBack() }
        )
    }
}