package com.ivzb.urgat.ui.library

import android.content.res.Configuration
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalResources
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
import com.ivzb.urgat.ui.components.EmptyStateScreen
import com.ivzb.urgat.ui.components.ItemHeaderWithSeeAll
import com.ivzb.urgat.ui.components.LoadingState
import com.ivzb.urgat.ui.components.MoreTodosMenu
import com.ivzb.urgat.ui.components.NavigationScaffold
import com.ivzb.urgat.ui.notes.NoteItem
import com.ivzb.urgat.ui.notes.NotesFilteringUiInfo
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.ui.todos.TodosFilteringUiInfo
import com.ivzb.urgat.ui.todos.TodoItem

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel,

    onAddTodo: () -> Unit,
    onSeeAllTodos: () -> Unit,
    onTodoClick: (Todo) -> Unit,

    onAddNote: () -> Unit,
    onSeeAllNotes: () -> Unit,
    onNoteClick: (Note) -> Unit,

    navigator: Navigator,

    modifier: Modifier = Modifier,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LibraryScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,

        onSeeAllTodos = onSeeAllTodos,
        onTodoClick = onTodoClick,

        onSeeAllNotes = onSeeAllNotes,
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

    val resources = LocalResources.current
    val itemsLimit = resources.getInteger(R.integer.items_limit)

    LaunchedEffect(itemsLimit) {
        viewModel.onIntent(LibraryIntent.ItemsLimitChanged(itemsLimit))
    }
}

@VisibleForTesting
@Composable
fun LibraryScreen(
    uiState: LibraryUiState,
    onIntent: (LibraryIntent) -> Unit,

    onSeeAllTodos: () -> Unit,
    onTodoClick: (Todo) -> Unit,

    onSeeAllNotes: () -> Unit,
    onNoteClick: (Note) -> Unit,

    navigator: Navigator,
    modifier: Modifier = Modifier
) {

    NavigationScaffold(
        selectedRoute = LibraryRoute,
        onSearchQueryChanged = { query -> onIntent(LibraryIntent.SearchQueryChanged(query)) },
        fabActions = uiState.fabActions,
        onFabActionClick = { id -> onIntent(LibraryIntent.FabActionClick(id)) },
        navigator = navigator,
        withTopAppBarActions = uiState.todos.isNotEmpty() || uiState.notes.isNotEmpty(),
        topAppBarActionsContent = {
            MoreTodosMenu(
                onClearCompletedTodos = { onIntent(LibraryIntent.ClearCompletedTodos) }
            )
        },
        modifier = modifier
    ) {

        when {
            uiState.isLoading -> {
                LoadingState(
                    uiState.filteringUiInfo.loadingLabel
                )
            }

            uiState.hasError -> {
                EmptyStateScreen(
                    label = uiState.filteringUiInfo.errorLabel,
                    iconRes = uiState.filteringUiInfo.errorIconRes,
                )
            }

            else -> {
                LibraryContent(
                    todos = uiState.todos,
                    todosFilteringUiInfo = uiState.todosFilteringUiInfo,
                    onAddTodo = { onIntent(LibraryIntent.FabActionClick(LibraryFabActionId.Todo)) },
                    onSeeAllTodos = onSeeAllTodos,
                    onTodoClick = onTodoClick,
                    onTodoCheckedChange = { todo, completed -> onIntent(LibraryIntent.CompleteTodo(todo, completed)) },

                    notes = uiState.notes,
                    notesFilteringUiInfo = uiState.notesFilteringUiInfo,
                    onAddNote = { onIntent(LibraryIntent.FabActionClick(LibraryFabActionId.Note)) },
                    onSeeAllNotes = onSeeAllNotes,
                    onNoteClick = onNoteClick,
                )
            }
        }

    }
}

@Composable
private fun LibraryContent(
    todos: List<Todo>,
    todosFilteringUiInfo: TodosFilteringUiInfo,
    onAddTodo: () -> Unit,
    onSeeAllTodos: () -> Unit,
    onTodoClick: (Todo) -> Unit,
    onTodoCheckedChange: (Todo, Boolean) -> Unit,

    notes: List<Note>,
    notesFilteringUiInfo: NotesFilteringUiInfo,
    onAddNote: () -> Unit,
    onSeeAllNotes: () -> Unit,
    onNoteClick: (Note) -> Unit,

    modifier: Modifier = Modifier
) {

    val configuration = LocalConfiguration.current

    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        LibraryContentLandscape(
            todos = todos,
            todosFilteringUiInfo = todosFilteringUiInfo,
            onAddTodo = onAddTodo,
            onSeeAllTodos = onSeeAllTodos,
            onTodoClick = onTodoClick,
            onTodoCheckedChange = onTodoCheckedChange,

            notes = notes,
            notesFilteringUiInfo = notesFilteringUiInfo,
            onAddNote = onAddNote,
            onSeeAllNotes = onSeeAllNotes,
            onNoteClick = onNoteClick,

            modifier = modifier,
        )
    } else {
        LibraryContentPortrait(
            todos = todos,
            todosFilteringUiInfo = todosFilteringUiInfo,
            onAddTodo = onAddTodo,
            onSeeAllTodos = onSeeAllTodos,
            onTodoClick = onTodoClick,
            onTodoCheckedChange = onTodoCheckedChange,

            notes = notes,
            notesFilteringUiInfo = notesFilteringUiInfo,
            onAddNote = onAddNote,
            onSeeAllNotes = onSeeAllNotes,
            onNoteClick = onNoteClick,

            modifier = modifier,
        )
    }
}

@Composable
private fun LibraryContentPortrait(
    todos: List<Todo>,
    todosFilteringUiInfo: TodosFilteringUiInfo,
    onAddTodo: () -> Unit,
    onSeeAllTodos: () -> Unit,
    onTodoClick: (Todo) -> Unit,
    onTodoCheckedChange: (Todo, Boolean) -> Unit,

    notes: List<Note>,
    notesFilteringUiInfo: NotesFilteringUiInfo,
    onAddNote: () -> Unit,
    onSeeAllNotes: () -> Unit,
    onNoteClick: (Note) -> Unit,

    modifier: Modifier = Modifier
) {
    val screenName = stringResource(R.string.library)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = screenName },
        contentPadding = PaddingValues(
            horizontal = dimensionResource(R.dimen.content_padding_horizontal),
            vertical = dimensionResource(R.dimen.content_padding_vertical),
        ),
    ) {

        itemTodo(
            todos = todos,
            todosFilteringUiInfo = todosFilteringUiInfo,
            onAddTodo = onAddTodo,
            onSeeAllTodos = onSeeAllTodos,
            onTodoClick = onTodoClick,
            onTodoCheckedChange = onTodoCheckedChange,
        )

        item {
            Spacer(
                modifier = Modifier
                    .height(dimensionResource(R.dimen.vertical_margin))
            )
        }

        itemNotes(
            notes = notes,
            notesFilteringUiInfo = notesFilteringUiInfo,
            onAddNote = onAddNote,
            onSeeAllNotes = onSeeAllNotes,
            onNoteClick = onNoteClick
        )

        item {
            Spacer(
                modifier = Modifier
                    .height(dimensionResource(R.dimen.vertical_margin))
            )
        }
    }
}

@Composable
private fun LibraryContentLandscape(
    todos: List<Todo>,
    todosFilteringUiInfo: TodosFilteringUiInfo,
    onAddTodo: () -> Unit,
    onSeeAllTodos: () -> Unit,
    onTodoClick: (Todo) -> Unit,
    onTodoCheckedChange: (Todo, Boolean) -> Unit,

    notes: List<Note>,
    notesFilteringUiInfo: NotesFilteringUiInfo,
    onAddNote: () -> Unit,
    onSeeAllNotes: () -> Unit,
    onNoteClick: (Note) -> Unit,

    modifier: Modifier = Modifier
) {
    val screenName = stringResource(R.string.library)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.content_padding_horizontal)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {

        LazyColumn(
            modifier = modifier
                .weight(1f)
                .semantics { contentDescription = screenName },
            contentPadding = PaddingValues(
                horizontal = dimensionResource(R.dimen.content_padding_horizontal),
                vertical = dimensionResource(R.dimen.content_padding_vertical),
            ),
        ) {

            itemTodo(
                todos = todos,
                todosFilteringUiInfo = todosFilteringUiInfo,
                onAddTodo = onAddTodo,
                onSeeAllTodos = onSeeAllTodos,
                onTodoClick = onTodoClick,
                onTodoCheckedChange = onTodoCheckedChange,
            )

            item {
                Spacer(
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.vertical_margin))
                )
            }
        }

        LazyColumn(
            modifier = modifier
                .weight(1f)
                .semantics { contentDescription = screenName },
            contentPadding = PaddingValues(
                horizontal = dimensionResource(R.dimen.content_padding_horizontal),
                vertical = dimensionResource(R.dimen.content_padding_vertical),
            ),
        ) {

            itemNotes(
                notes = notes,
                notesFilteringUiInfo = notesFilteringUiInfo,
                onAddNote = onAddNote,
                onSeeAllNotes = onSeeAllNotes,
                onNoteClick = onNoteClick
            )

            item {
                Spacer(
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.vertical_margin))
                )
            }
        }
    }
}

private fun LazyListScope.itemTodo(
    todos: List<Todo>,
    todosFilteringUiInfo: TodosFilteringUiInfo,
    onAddTodo: () -> Unit,
    onSeeAllTodos: () -> Unit,
    onTodoClick: (Todo) -> Unit,
    onTodoCheckedChange: (Todo, Boolean) -> Unit
) {
    when {
        todos.isEmpty() -> {
            item {
                EmptyStateCard(
                    iconRes = todosFilteringUiInfo.emptyIconRes,
                    label = todosFilteringUiInfo.emptyLabel,
                    message = todosFilteringUiInfo.emptyMessage,
                    buttonLabel = todosFilteringUiInfo.emptyButtonLabel,
                    onButtonClick = onAddTodo,
                )
            }
        }

        else -> {
            item {
                ItemHeaderWithSeeAll(
                    title = stringResource(todosFilteringUiInfo.currentFilteringLabel),
                    onSeeAllClick = onSeeAllTodos,
                )
            }

            items(
                items = todos,
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

private fun LazyListScope.itemNotes(
    notes: List<Note>,
    notesFilteringUiInfo: NotesFilteringUiInfo,
    onAddNote: () -> Unit,
    onSeeAllNotes: () -> Unit,
    onNoteClick: (Note) -> Unit,
) {
    when {
        notes.isEmpty() -> {
            item {
                EmptyStateCard(
                    iconRes = notesFilteringUiInfo.emptyIconRes,
                    label = notesFilteringUiInfo.emptyLabel,
                    message = notesFilteringUiInfo.emptyMessage,
                    buttonLabel = notesFilteringUiInfo.emptyButtonLabel,
                    onButtonClick = onAddNote,
                )
            }
        }

        else -> {
            item {
                ItemHeaderWithSeeAll(
                    title = stringResource(notesFilteringUiInfo.currentFilteringLabel),
                    onSeeAllClick = onSeeAllNotes,
                )
            }

            items(
                items = notes,
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

@AllDevicesPreview
@Composable
fun LibraryScreenPreview() {
    UrgatTheme {
        LibraryScreen(
            uiState = LibraryUiState(
                todos = SampleData.SAMPLE_TODOS.take(3),
                todosFilteringUiInfo = TodosFilteringUiInfo(
                    currentFilteringLabel = R.string.recent_todos,
                ),
                notes = SampleData.SAMPLE_NOTES.take(3),
                notesFilteringUiInfo = NotesFilteringUiInfo(
                    currentFilteringLabel = R.string.recent_notes,
                ),                isLoading = false,
                hasError = false,
                filteringUiInfo = LibraryFilteringUiInfo()
            ),
            onIntent = {},
            onSeeAllTodos = {},
            onTodoClick = {},
            onSeeAllNotes = {},
            onNoteClick = {},
            navigator = FakeNavigator(),
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
fun LibraryScreenEmptyPreview() {
    UrgatTheme {
        LibraryScreen(
            uiState = LibraryUiState(
                todos = listOf(),
                todosFilteringUiInfo = TodosFilteringUiInfo(),
                notes = listOf(),
                notesFilteringUiInfo = NotesFilteringUiInfo(),
                isLoading = false,
                hasError = false,
                filteringUiInfo = LibraryFilteringUiInfo()
            ),
            onIntent = {},
            onSeeAllTodos = {},
            onTodoClick = {},
            onSeeAllNotes = {},
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
                todos = listOf(),
                todosFilteringUiInfo = TodosFilteringUiInfo(),
                notes = listOf(),
                notesFilteringUiInfo = NotesFilteringUiInfo(),
                isLoading = true,
                hasError = false,
                filteringUiInfo = LibraryFilteringUiInfo()
            ),
            onIntent = {},
            onSeeAllTodos = {},
            onTodoClick = {},
            onSeeAllNotes = {},
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
                todos = listOf(),
                todosFilteringUiInfo = TodosFilteringUiInfo(),
                notes = listOf(),
                notesFilteringUiInfo = NotesFilteringUiInfo(),
                isLoading = false,
                hasError = true,
                filteringUiInfo = LibraryFilteringUiInfo()
            ),
            onIntent = {},
            onSeeAllTodos = {},
            onTodoClick = {},
            onSeeAllNotes = {},
            onNoteClick = {},
            navigator = FakeNavigator(),
            modifier = Modifier
        )
    }
}