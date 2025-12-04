package com.ivzb.urgat.ui.addEditNote

import androidx.annotation.StringRes

data class AddEditNoteUiState(
    @StringRes
    val screenTitleResId: Int? = null,
    val content: String = "",
    val focusIndex: Int = 0,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val withDelete: Boolean = false,
    val shouldNavigateBack: Boolean = false
)
