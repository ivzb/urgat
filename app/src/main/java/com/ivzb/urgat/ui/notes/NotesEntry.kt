package com.ivzb.urgat.ui.notes

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator
import com.ivzb.urgat.navigation.UrgatNavActions

fun EntryProviderScope<NavKey>.notesEntry(navigator: Navigator, navActions: UrgatNavActions) {
    entry<NotesRoute> {
        NotesScreen(
            viewModel = hiltViewModel(),

            onAddNote = { navActions.navigateToAddNote() },
            onNoteClick = { navActions.navigateToEditNote(it.id) },
            onBack = { navigator.goBack() }
        )
    }
}