package com.ivzb.urgat.ui.exportData

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator

fun EntryProviderScope<NavKey>.exportDataEntry(navigator: Navigator) {
    entry<ExportDataRoute> {
        ExportDataScreen(
            viewModel = hiltViewModel(),

            onBack = { navigator.goBack() }
        )
    }
}