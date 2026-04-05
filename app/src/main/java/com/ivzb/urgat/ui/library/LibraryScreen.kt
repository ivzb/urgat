package com.ivzb.urgat.ui.library

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.navigation.FakeNavigator
import com.ivzb.urgat.navigation.Navigator
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.EmptyStateCard
import com.ivzb.urgat.ui.components.FilterMenu
import com.ivzb.urgat.ui.components.MoreMenu
import com.ivzb.urgat.ui.components.screens.NavigationPagerScreen
import com.ivzb.urgat.ui.notes.NoteItem
import com.ivzb.urgat.ui.notes.NotesFilteringUiInfo
import com.ivzb.urgat.ui.notes.NotesUiState
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.ui.todos.TodoItem
import com.ivzb.urgat.ui.todos.TodosFilteringUiInfo
import com.ivzb.urgat.ui.todos.TodosUiState

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel,

    onAddTodo: () -> Unit,
    onTodoClick: (Todo) -> Unit,

    onAddNote: () -> Unit,
    onNoteClick: (Note) -> Unit,

    navigator: Navigator,
    modifier: Modifier = Modifier,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LibraryScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,

        onTodoClick = onTodoClick,
        onNoteClick = onNoteClick,

        navigator = navigator,
        modifier = modifier
    )

    LaunchedEffect(uiState.uiEvent) {
        uiState.uiEvent?.let { event ->
            when (event) {
                is LibraryUiEvent.AddTodo -> onAddTodo()
                is LibraryUiEvent.AddNote -> onAddNote()
            }

            viewModel.onIntent(LibraryIntent.EventConsumed)
        }
    }
}

@VisibleForTesting
@Composable
fun LibraryScreen(
    uiState: LibraryUiState,
    onIntent: (LibraryIntent) -> Unit,

    onTodoClick: (Todo) -> Unit,
    onNoteClick: (Note) -> Unit,

    navigator: Navigator,
    modifier: Modifier = Modifier
) {

    NavigationPagerScreen(
        uiState = uiState,

        route = LibraryRoute,
        searchQuery = uiState.filteringUiInfo.searchQuery,
        onSearchQueryChanged = { query -> onIntent(LibraryIntent.SearchQueryChanged(query)) },
        onFabActionClick = { page -> onIntent(LibraryIntent.FabActionClick(page)) },
        navigator = navigator,

        withSearch = { page -> uiState.withTopbarActions(page) },
        topAppBarActionsContent = { page ->
            if (uiState.withTopbarActions(page)) {
                when (page) {
                    LibraryPage.Todos -> {
                        FilterMenu(
                            items = uiState.todosUiState.filteringUiInfo.filterMenuItems,
                            showBadge = uiState.todosUiState.filteringUiInfo.showFilterBadge,
                            onFilter = { onIntent(LibraryIntent.SetTodosFiltering(it)) },
                        )
                    }
                }

                MoreMenu(
                    listOfNotNull(
                        (R.string.clear_completed to { onIntent(LibraryIntent.ClearCompletedTodos) }).takeIf { page == LibraryPage.Todos && uiState.todosUiState.hasCompletedTodos },
                        (R.string.reset_filters to { onIntent(LibraryIntent.ResetFilters) }).takeIf { uiState.hasAppliedFilters },
                    )
                )
            }
        },

        content = { page, listState, onListStateChanged ->
            when (page) {
                LibraryPage.Todos -> TodosPage(
                    uiState = uiState.todosUiState,
                    onAddTodo = { onIntent(LibraryIntent.FabActionClick(page)) },
                    onTodoClick = onTodoClick,
                    onTodoCheckedChange = { todo, completed -> onIntent(LibraryIntent.CompleteTodo(todo, completed)) },
                    onResetFilters = { onIntent(LibraryIntent.ResetFilters) },

                    listState = listState,
                    onListStateChanged = onListStateChanged,
                )

                LibraryPage.Notes -> NotesPage(
                    uiState = uiState.notesUiState,
                    onAddNote = { onIntent(LibraryIntent.FabActionClick(page)) },
                    onNoteClick = onNoteClick,
                    onResetFilters = { onIntent(LibraryIntent.ResetFilters) },

                    listState = listState,
                    onListStateChanged = onListStateChanged,
                )
            }
        },

        modifier = modifier
    )
}

@Composable
private fun TodosPage(
    uiState: TodosUiState,
    onAddTodo: () -> Unit,
    onTodoClick: (Todo) -> Unit,
    onTodoCheckedChange: (Todo, Boolean) -> Unit,
    onResetFilters: () -> Unit,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    LaunchedEffect(Unit) {
        onListStateChanged(listState)
    }

    val screenName = stringResource(R.string.finance)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = screenName },
        contentPadding = PaddingValues(
            horizontal = dimensionResource(R.dimen.content_padding),
            vertical = dimensionResource(R.dimen.content_padding),
        ),
        state = listState,
    ) {

        when {
            uiState.todos.isEmpty() -> {
                item {
                    EmptyStateCard(
                        iconRes = uiState.filteringUiInfo.emptyIconRes,
                        label = uiState.filteringUiInfo.emptyLabel,
                        message = uiState.filteringUiInfo.emptyMessage,
                        buttonLabel = uiState.filteringUiInfo.emptyButtonLabel,
                        onButtonClick = if (uiState.filteringUiInfo.hasAppliedFilters) onResetFilters else onAddTodo,
                    )
                }
            }

            else -> {
                items(
                    items = uiState.todos,
                    key = { todo -> todo.id },
                ) { todo ->

                    TodoItem(
                        todo = todo,
                        onTodoClick = onTodoClick,
                        onCheckedChange = { onTodoCheckedChange(todo, it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotesPage(
    uiState: NotesUiState,
    onAddNote: () -> Unit,
    onNoteClick: (Note) -> Unit,
    onResetFilters: () -> Unit,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    LaunchedEffect(Unit) {
        onListStateChanged(listState)
    }

    val screenName = stringResource(R.string.finance)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = screenName },
        contentPadding = PaddingValues(
            horizontal = dimensionResource(R.dimen.content_padding),
            vertical = dimensionResource(R.dimen.content_padding),
        ),
        state = listState,
    ) {

        when {
            uiState.notes.isEmpty() -> {
                item {
                    EmptyStateCard(
                        iconRes = uiState.filteringUiInfo.emptyIconRes,
                        label = uiState.filteringUiInfo.emptyLabel,
                        message = uiState.filteringUiInfo.emptyMessage,
                        buttonLabel = uiState.filteringUiInfo.emptyButtonLabel,
                        onButtonClick = if (uiState.filteringUiInfo.hasAppliedFilters) onResetFilters else onAddNote,
                    )
                }
            }

            else -> {
                items(
                    items = uiState.notes,
                    key = { note -> note.id },
                ) { note ->

                    NoteItem(
                        note = note,
                        onNoteClick = onNoteClick
                    )
                }
            }
        }
    }
}

@AllDevicesPreview
@Composable
fun LibraryScreenPreview(initialPage: Int = 0) {
    UrgatTheme {
        LibraryScreen(
            uiState = LibraryUiState(
                todosUiState = TodosUiState(
                    todos = SampleData.SAMPLE_TODOS.take(5),
                    filteringUiInfo = TodosFilteringUiInfo(
                        currentFilteringLabel = R.string.all_todos,
                    ),
                ),
                notesUiState = NotesUiState(
                    notes = SampleData.SAMPLE_NOTES.take(5),
                    filteringUiInfo = NotesFilteringUiInfo(
                        currentFilteringLabel = R.string.all_notes,
                    ),
                ),
                isLoading = false,
                hasError = false,
                initialPage = initialPage,
                filteringUiInfo = LibraryFilteringUiInfo()
            ),
            onIntent = {},
            onTodoClick = {},
            onNoteClick = {},
            navigator = FakeNavigator(),
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
fun LibraryScreenNotesPreview() {
    LibraryScreenPreview(initialPage = 0)
}

@AllDevicesPreview
@Composable
fun LibraryScreenEmptyPreview() {
    UrgatTheme {
        LibraryScreen(
            uiState = LibraryUiState(
                todosUiState = TodosUiState(),
                notesUiState = NotesUiState(),
                isLoading = false,
                hasError = false,
                filteringUiInfo = LibraryFilteringUiInfo()
            ),
            onIntent = {},
            onTodoClick = {},
            onNoteClick = {},
            navigator = FakeNavigator(),
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
fun LibraryScreenLoadingPreview() {
    UrgatTheme {
        LibraryScreen(
            uiState = LibraryUiState(
                todosUiState = TodosUiState(),
                notesUiState = NotesUiState(),
                isLoading = true,
                hasError = false,
                filteringUiInfo = LibraryFilteringUiInfo()
            ),
            onIntent = {},
            onTodoClick = {},
            onNoteClick = {},
            navigator = FakeNavigator(),
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
fun LibraryScreenErrorPreview() {
    UrgatTheme {
        LibraryScreen(
            uiState = LibraryUiState(
                todosUiState = TodosUiState(),
                notesUiState = NotesUiState(),
                isLoading = false,
                hasError = true,
                filteringUiInfo = LibraryFilteringUiInfo()
            ),
            onIntent = {},
            onTodoClick = {},
            onNoteClick = {},
            navigator = FakeNavigator(),
            modifier = Modifier
        )
    }
}