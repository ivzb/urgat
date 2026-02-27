package com.ivzb.urgat.ui.notes

import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.ui.components.uiState.UiState

data class NotesUiState(
    val notes: List<Note> = listOf(),

    override val isLoading: Boolean = false,
    override val hasError: Boolean = false,
    override val isEmpty: Boolean = notes.isEmpty(),

    override val filteringUiInfo: NotesFilteringUiInfo = NotesFilteringUiInfo(),
): UiState