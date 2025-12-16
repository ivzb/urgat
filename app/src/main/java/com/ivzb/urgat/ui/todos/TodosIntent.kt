package com.ivzb.urgat.ui.todos

import com.ivzb.urgat.data.todo.Todo

sealed class TodosIntent {
    data class SearchQueryChanged(val query: String): TodosIntent()
    data class SetFiltering(val filterType: TodosFilterType): TodosIntent()
    object ClearCompletedTodos: TodosIntent()
    data class CompleteTodos(val todo: Todo, val completed: Boolean): TodosIntent()
}