package com.ivzb.urgat.ui.library

import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.components.uiState.NavigationPagerUiState
import com.ivzb.urgat.ui.notes.NotesUiState
import com.ivzb.urgat.ui.todos.TodosUiState

data class LibraryUiState(
    override val fabActions: List<LibraryFabAction> = LIBRARY_FAB_ACTIONS,

    val todosUiState: TodosUiState = TodosUiState(),
    val notesUiState: NotesUiState = NotesUiState(),

    override val isLoading: Boolean = false,
    override val hasError: Boolean = false,
    override val isEmpty: Boolean = false,

    override val pages: List<Page> = LIBRARY_PAGES,
    override val initialPage: Int = 0,

    override val filteringUiInfo: LibraryFilteringUiInfo = LibraryFilteringUiInfo(),

    val uiEvent: LibraryUiEvent? = null,
): NavigationPagerUiState<LibraryFabActionId> {

    val hasAppliedFilters: Boolean =
        todosUiState.filteringUiInfo.hasAppliedFilters ||
        notesUiState.filteringUiInfo.hasAppliedFilters

    fun withTopbarActions(page: Page): Boolean {
        val uiState = when (page) {
            LibraryPage.Todos -> todosUiState
            LibraryPage.Notes -> notesUiState
            else -> null
        }

        return uiState?.let { !it.isEmpty || it.filteringUiInfo.searchQuery.isNotEmpty() } ?: false
    }

}