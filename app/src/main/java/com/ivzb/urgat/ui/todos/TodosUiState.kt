package com.ivzb.urgat.ui.todos

import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.ui.components.uiState.UiState

data class TodosUiState(
    val items: List<Todo> = emptyList(),

    override val isLoading: Boolean = false,
    override val hasError: Boolean = false,
    override val isEmpty: Boolean = items.isEmpty(),

    override val filteringUiInfo: TodosFilteringUiInfo = TodosFilteringUiInfo()
): UiState
