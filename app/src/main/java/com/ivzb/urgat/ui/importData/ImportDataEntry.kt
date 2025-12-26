package com.ivzb.urgat.ui.importData

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator

fun EntryProviderScope<NavKey>.importDataEntry(navigator: Navigator) {
    entry<ImportDataRoute> {
        ImportDataScreen(
            viewModel = hiltViewModel(),

            onBack = { navigator.goBack() }
        )
    }
}