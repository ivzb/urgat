package com.ivzb.urgat.ui.library

import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.ui.components.FilteringUiInfo
import com.ivzb.urgat.ui.notes.NotesFilteringUiInfo
import com.ivzb.urgat.ui.todos.TodosFilteringUiInfo

data class LibraryUiState(
    val fabActions: List<LibraryFabAction> = LIBRARY_FAB_ACTIONS,

    val todos: List<Todo> = emptyList(),
    val todosFilteringUiInfo: TodosFilteringUiInfo = TodosFilteringUiInfo(),

    val notes: List<Note> = emptyList(),
    val notesFilteringUiInfo: NotesFilteringUiInfo = NotesFilteringUiInfo(),

    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val filteringUiInfo: FilteringUiInfo = LibraryFilteringUiInfo(),

    val uiEvent: LibraryUiEvent? = null,
)