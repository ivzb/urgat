package com.ivzb.urgat.ui.notes

import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.ui.listDetail.ListDetailUiState

data class NotesUiState(
    val items: List<Note> = emptyList(),

    override val isLoading: Boolean = false,
    override val hasError: Boolean = false,
    override val isEmpty: Boolean = items.isEmpty(),

    override val filteringUiInfo: NotesFilteringUiInfo = NotesFilteringUiInfo()
): ListDetailUiState
