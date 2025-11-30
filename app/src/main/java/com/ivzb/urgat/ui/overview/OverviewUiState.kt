package com.ivzb.urgat.ui.overview

import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.ui.components.FilteringUiInfo
import com.ivzb.urgat.ui.notes.NotesFilteringUiInfo
import com.ivzb.urgat.ui.todo.TodoFilteringUiInfo

data class OverviewUiState(
    val headerItems: List<OverviewHeaderItem> = emptyList(),

    val todos: List<Todo> = emptyList(),
    val todosFilteringUiInfo: TodoFilteringUiInfo = TodoFilteringUiInfo(),

    val notes: List<Note> = emptyList(),
    val notesFilteringUiInfo: NotesFilteringUiInfo = NotesFilteringUiInfo(),

    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val filteringUiInfo: FilteringUiInfo = OverviewFilteringUiInfo(),

    val uiEvent: OverviewUiEvent? = null,
)