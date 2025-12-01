package com.ivzb.urgat.ui.library

import com.ivzb.urgat.data.todo.Todo

sealed class LibraryIntent {
    data class SearchQueryChanged(val query: String): LibraryIntent()
    object ClearCompletedTodos: LibraryIntent()
    data class CompleteTodo(val todo: Todo, val completed: Boolean): LibraryIntent()
    data class HeaderItemClick(val itemId: LibraryHeaderItemId): LibraryIntent()
    object EventConsumed: LibraryIntent()
}