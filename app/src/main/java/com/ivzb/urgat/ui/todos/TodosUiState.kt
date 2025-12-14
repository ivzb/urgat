package com.ivzb.urgat.ui.todos

import com.ivzb.urgat.data.todo.Todo

data class TodosUiState(
    val items: List<Todo> = emptyList(),
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val filteringUiInfo: TodosFilteringUiInfo = TodosFilteringUiInfo()
)
