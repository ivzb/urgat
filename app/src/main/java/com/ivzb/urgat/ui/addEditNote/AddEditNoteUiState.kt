package com.ivzb.urgat.ui.addEditNote

data class AddEditNoteUiState(
    val content: String = "",
    val focusIndex: Int = 0,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val isNoteSaved: Boolean = false
)
