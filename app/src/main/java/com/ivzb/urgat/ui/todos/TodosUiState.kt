package com.ivzb.urgat.ui.todos

import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.ui.components.uiState.UiState

data class TodosUiState(
    val todos: List<Todo> = listOf(),

    override val isLoading: Boolean = false,
    override val hasError: Boolean = false,
    override val isEmpty: Boolean = todos.isEmpty(),

    override val filteringUiInfo: TodosFilteringUiInfo = TodosFilteringUiInfo(),
): UiState