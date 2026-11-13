package com.ivzb.urgat.ui.library

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator
import com.ivzb.urgat.navigation.UrgatNavActions
import com.ivzb.urgat.navigation.navigateToAddNote
import com.ivzb.urgat.navigation.navigateToAddTodo
import com.ivzb.urgat.navigation.navigateToEditNote
import com.ivzb.urgat.navigation.navigateToEditTodo

fun EntryProviderScope<NavKey>.libraryEntry(navigator: Navigator, navActions: UrgatNavActions) {
    entry<LibraryRoute> {
        LibraryScreen(
            viewModel = hiltViewModel(),

            onAddTodo = { navActions.navigateToAddTodo() },
            onTodoClick = { navActions.navigateToEditTodo(it.id) },

            onAddNote = { navActions.navigateToAddNote() },
            onNoteClick = { navActions.navigateToEditNote(it.id) },

            navigator = navigator
        )
    }
}
