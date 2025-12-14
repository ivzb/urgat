package com.ivzb.urgat.ui.todos

import com.ivzb.urgat.data.todo.Todo

sealed class TodosIntent {
    data class SearchQueryChanged(val query: String): TodosIntent()
    data class SetFiltering(val filter: TodosFilterMenuItem): TodosIntent()
    object ClearCompletedTodos: TodosIntent()
    data class CompleteTodos(val todo: Todo, val completed: Boolean): TodosIntent()
}