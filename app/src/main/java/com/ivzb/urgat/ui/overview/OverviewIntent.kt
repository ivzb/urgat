package com.ivzb.urgat.ui.overview

import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.ui.todo.TodosFilterType

sealed class OverviewIntent {
    data class SearchQueryChanged(val query: String): OverviewIntent()
    object ClearCompletedTodos: OverviewIntent()
    data class CompleteTodo(val todo: Todo, val completed: Boolean): OverviewIntent()
    data class HeaderItemClick(val item: HeaderItem): OverviewIntent()
}