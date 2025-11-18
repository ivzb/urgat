package com.ivzb.urgat.ui.todo

import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.ui.components.FilterMenuItem

sealed class TodoIntent {
    data class SearchQueryChanged(val query: String): TodoIntent()
    data class SetFiltering(val filter: FilterMenuItem): TodoIntent()
    object ClearCompletedTodos: TodoIntent()
    data class CompleteTodo(val todo: Todo, val completed: Boolean): TodoIntent()
}