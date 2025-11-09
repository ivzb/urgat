package com.ivzb.urgat.ui.overview

import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.ui.components.HeaderItem
import com.ivzb.urgat.ui.trackers.TrackersIntent

sealed class OverviewIntent {
    data class SearchQueryChanged(val query: String): OverviewIntent()
    object ClearCompletedTodos: OverviewIntent()
    data class CompleteTodo(val todo: Todo, val completed: Boolean): OverviewIntent()
    data class HeaderItemClick(val itemId: OverviewHeaderItemId): OverviewIntent()
    object EventConsumed: OverviewIntent()
}