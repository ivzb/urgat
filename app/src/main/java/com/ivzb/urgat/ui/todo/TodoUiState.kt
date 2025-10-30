package com.ivzb.urgat.ui.todo

import com.ivzb.urgat.data.todo.Todo

data class TodoUiState(
    val items: List<Todo> = emptyList(),
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val filteringUiInfo: TodoFilteringUiInfo = TodoFilteringUiInfo()
)
