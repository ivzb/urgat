package com.ivzb.urgat.ui.notes

import com.ivzb.urgat.data.note.Note

data class NotesUiState(
    val items: List<Note> = emptyList(),
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val filteringUiInfo: NotesFilteringUiInfo = NotesFilteringUiInfo()
)
