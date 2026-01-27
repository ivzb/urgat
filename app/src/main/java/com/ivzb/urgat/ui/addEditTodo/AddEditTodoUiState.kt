package com.ivzb.urgat.ui.addEditTodo

import androidx.annotation.StringRes

data class AddEditTodoUiState(
    @StringRes
    val screenTitleResId: Int? = null,
    val content: String = "",
    val focusIndex: Int = 0,
    val isTodoCompleted: Boolean = false,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val withDelete: Boolean = false,
    val shouldNavigateBack: Boolean = false
)
