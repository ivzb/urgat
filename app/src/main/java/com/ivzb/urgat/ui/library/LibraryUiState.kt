package com.ivzb.urgat.ui.library

import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.ui.components.FilteringUiInfo
import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.components.uiState.NavigationPagerUiState
import com.ivzb.urgat.ui.notes.NotesFilteringUiInfo
import com.ivzb.urgat.ui.todos.TodosFilteringUiInfo

data class LibraryUiState(
    override val fabActions: List<LibraryFabAction> = LIBRARY_FAB_ACTIONS,

    val todos: List<Todo> = listOf(),
    val todosFilteringUiInfo: TodosFilteringUiInfo = TodosFilteringUiInfo(),

    val notes: List<Note> = listOf(),
    val notesFilteringUiInfo: NotesFilteringUiInfo = NotesFilteringUiInfo(),

    override val isLoading: Boolean = false,
    override val hasError: Boolean = false,
    override val isEmpty: Boolean = false,

    override val pages: List<Page> = LIBRARY_PAGES,
    override val initialPage: Int = 0,

    override val filteringUiInfo: FilteringUiInfo = LibraryFilteringUiInfo(),

    val uiEvent: LibraryUiEvent? = null,
): NavigationPagerUiState<LibraryFabActionId>