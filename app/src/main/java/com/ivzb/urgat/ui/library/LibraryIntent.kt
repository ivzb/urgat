package com.ivzb.urgat.ui.library

import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.ui.todos.TodosFilterType

sealed class LibraryIntent {
    data class SearchQueryChanged(val query: String): LibraryIntent()
    data class SetTodosFiltering(val filterType: TodosFilterType): LibraryIntent()
    object ClearCompletedTodos: LibraryIntent()
    data class CompleteTodo(val todo: Todo, val completed: Boolean): LibraryIntent()
    data class FabActionClick(val itemId: LibraryFabActionId): LibraryIntent()
    object EventConsumed: LibraryIntent()
}