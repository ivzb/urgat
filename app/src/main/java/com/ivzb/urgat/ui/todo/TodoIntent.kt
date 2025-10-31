package com.ivzb.urgat.ui.todo

import com.ivzb.urgat.data.todo.Todo

sealed class TodoIntent {
    data class SearchQueryChanged(val query: String): TodoIntent()
    data class SetFiltering(val filterType: TodosFilterType): TodoIntent()
    object ClearCompletedTodos: TodoIntent()
    data class CompleteTodo(val todo: Todo, val completed: Boolean): TodoIntent()
}