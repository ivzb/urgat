package com.ivzb.urgat.ui.addEditWatchlistAsset

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator

fun EntryProviderScope<NavKey>.addEditWatchlistAssetEntry(navigator: Navigator) {
    entry<AddEditWatchlistAssetRoute> { route ->
        val viewModel = hiltViewModel<AddEditWatchlistAssetViewModel, AddEditWatchlistAssetViewModel.Factory>(
            creationCallback = { factory ->
                factory.create(route)
            }
        )

        AddEditWatchlistAssetScreen(
            viewModel = viewModel,

            onBack = { navigator.goBack() },
        )
    }
}

