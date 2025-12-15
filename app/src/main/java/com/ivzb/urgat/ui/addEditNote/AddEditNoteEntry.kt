package com.ivzb.urgat.ui.addEditNote

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator

fun EntryProviderScope<NavKey>.addEditNoteEntry(navigator: Navigator) {
    entry<AddEditNoteRoute> { route ->
        val viewModel = hiltViewModel<AddEditNoteViewModel, AddEditNoteViewModel.Factory>(
            creationCallback = { factory ->
                factory.create(route)
            }
        )

        AddEditNoteScreen(
            viewModel = viewModel,

            onBack = { navigator.goBack() },
        )
    }
}