package com.ivzb.urgat.ui.addEditTodo

data class AddEditTodoUiState(
    val title: String = "",
    val description: String = "",
    val isTodoCompleted: Boolean = false,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val isTodoSaved: Boolean = false
)
