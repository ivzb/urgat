package com.ivzb.urgat.ui.library

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator
import com.ivzb.urgat.navigation.UrgatNavActions

fun EntryProviderScope<NavKey>.libraryEntry(navigator: Navigator, navActions: UrgatNavActions) {
    entry<LibraryRoute> {
        LibraryScreen(
            viewModel = hiltViewModel(),

            onAddTodo = { navActions.navigateToAddTodo() },
            onSeeAllTodos = { navActions.navigateToTodos() },
            onTodoClick = { navActions.navigateToEditTodo(it.id) },

            onAddNote = { navActions.navigateToAddNote() },
            onSeeAllNotes = { navActions.navigateToNotes() },
            onNoteClick = { navActions.navigateToEditNote(it.id) },

            navigator = navigator
        )
    }
}