package com.ivzb.urgat.ui.library

import android.content.res.Configuration
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ivzb.urgat.R
import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.navigation.LibraryDestination
import com.ivzb.urgat.ui.components.EmptyStateCard
import com.ivzb.urgat.ui.components.EmptyStateScreen
import com.ivzb.urgat.ui.components.Header
import com.ivzb.urgat.ui.components.ItemHeaderWithSeeAll
import com.ivzb.urgat.ui.components.LandscapePreview
import com.ivzb.urgat.ui.components.LoadingState
import com.ivzb.urgat.ui.components.MenuScaffold
import com.ivzb.urgat.ui.components.MoreTodosMenu
import com.ivzb.urgat.ui.notes.NoteItem
import com.ivzb.urgat.ui.notes.NotesFilteringUiInfo
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.ui.todo.TodoFilteringUiInfo
import com.ivzb.urgat.ui.todo.TodoItem

@Composable
fun LibraryScreen(
    onAddTodo: () -> Unit,
    onSeeAllTodos: () -> Unit,
    onTodoClick: (Todo) -> Unit,

    onAddNote: () -> Unit,
    onSeeAllNotes: () -> Unit,
    onNoteClick: (Note) -> Unit,

    navController: NavHostController,

    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LibraryScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,

        onSeeAllTodos = onSeeAllTodos,
        onTodoClick = onTodoClick,

        onSeeAllNotes = onSeeAllNotes,
        onNoteClick = onNoteClick,

        navController = navController,
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

    onSeeAllTodos: () -> Unit,
    onTodoClick: (Todo) -> Unit,

    onSeeAllNotes: () -> Unit,
    onNoteClick: (Note) -> Unit,

    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    MenuScaffold(
        selectedDestination = LibraryDestination,
        onSearchQueryChanged = { query -> onIntent(LibraryIntent.SearchQueryChanged(query)) },
        navController = navController,
        withTopAppBarActions = uiState.todos.isNotEmpty() || uiState.notes.isNotEmpty(),
        topAppBarActionsContent = {
            MoreTodosMenu(
                onClearCompletedTodos = { onIntent(LibraryIntent.ClearCompletedTodos) }
            )
        },
        modifier = modifier
    ) { listState ->

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
                    headerItems = uiState.headerItems,
                    onHeaderItemClick = { id -> onIntent(LibraryIntent.HeaderItemClick(id)) },
                    todos = uiState.todos,
                    todoFilteringUiInfo = uiState.todosFilteringUiInfo,
                    onSeeAllTodos = onSeeAllTodos,
                    onTodoClick = onTodoClick,
                    onTodoCheckedChange = { todo, completed -> onIntent(LibraryIntent.CompleteTodo(todo, completed)) },

                    notes = uiState.notes,
                    notesFilteringUiInfo = uiState.notesFilteringUiInfo,
                    onSeeAllNotes = onSeeAllNotes,
                    onNoteClick = onNoteClick,

                    listState = listState,
                )
            }
        }

    }
}

@Composable
private fun LibraryContent(
    headerItems: List<LibraryHeaderItem>,
    onHeaderItemClick: (LibraryHeaderItemId) -> Unit,

    todos: List<Todo>,
    todoFilteringUiInfo: TodoFilteringUiInfo,
    onSeeAllTodos: () -> Unit,
    onTodoClick: (Todo) -> Unit,
    onTodoCheckedChange: (Todo, Boolean) -> Unit,

    notes: List<Note>,
    notesFilteringUiInfo: NotesFilteringUiInfo,
    onSeeAllNotes: () -> Unit,
    onNoteClick: (Note) -> Unit,

    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    val screenName = stringResource(R.string.library)
    val configuration = LocalConfiguration.current
    val orientation = configuration.orientation

    Row (
        modifier = Modifier.fillMaxSize()
    ) {

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Header(
                items = headerItems,
                onItemClick = { onHeaderItemClick(it.id) },
                orientation
            )
        }

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .semantics { contentDescription = screenName },
            contentPadding = PaddingValues(
                horizontal = dimensionResource(R.dimen.horizontal_margin),
                vertical = dimensionResource(R.dimen.vertical_margin)
            ),
            state = listState
        ) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                item {
                    Header(
                        items = headerItems,
                        onItemClick = { onHeaderItemClick(it.id) },
                        orientation
                    )
                }
            }

            item {
                Spacer(
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.vertical_margin))
                )
            }

            itemTodo(
                todos = todos,
                todoFilteringUiInfo = todoFilteringUiInfo,
                onAddTodo = { onHeaderItemClick(LibraryHeaderItemId.Todo) },
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
                onAddNote = { onHeaderItemClick(LibraryHeaderItemId.Note) },
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
    todoFilteringUiInfo: TodoFilteringUiInfo,
    onAddTodo: () -> Unit,
    onSeeAllTodos: () -> Unit,
    onTodoClick: (Todo) -> Unit,
    onTodoCheckedChange: (Todo, Boolean) -> Unit
) {
    when {
        todos.isEmpty() -> {
            item {
                EmptyStateCard(
                    iconRes = todoFilteringUiInfo.emptyIconRes,
                    label = todoFilteringUiInfo.emptyLabel,
                    message = todoFilteringUiInfo.emptyMessage,
                    buttonLabel = todoFilteringUiInfo.emptyButtonLabel,
                    onButtonClick = onAddTodo,
                )
            }
        }

        else -> {
            item {
                ItemHeaderWithSeeAll(
                    title = stringResource(todoFilteringUiInfo.currentFilteringLabel),
                    onSeeAllClick = onSeeAllTodos,
                )
            }

            items(todos) { todo ->
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

            items(notes) { note ->
                NoteItem(
                    note = note,
                    onNoteClick = onNoteClick
                )
            }
        }
    }
}

@Preview
@LandscapePreview
@Composable
fun LibraryScreenPreview() {
    UrgatTheme {
        LibraryScreen(
            uiState = LibraryUiState(
                todos = SampleData.SAMPLE_TODOS.take(3),
                todosFilteringUiInfo = TodoFilteringUiInfo(),
                notes = SampleData.SAMPLE_NOTES.take(3),
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
            navController = rememberNavController(),
            modifier = Modifier
        )
    }
}

@Preview
@LandscapePreview
@Composable
fun LibraryScreenEmptyTodosPreview() {
    UrgatTheme {
        LibraryScreen(
            uiState = LibraryUiState(
                todos = listOf(),
                todosFilteringUiInfo = TodoFilteringUiInfo(),
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
            navController = rememberNavController(),
            modifier = Modifier
        )
    }
}

@Preview
@LandscapePreview
@Composable
fun LibraryScreenLoadingPreview() {
    UrgatTheme {
        LibraryScreen(
            uiState = LibraryUiState(
                todos = listOf(),
                todosFilteringUiInfo = TodoFilteringUiInfo(),
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
            navController = rememberNavController(),
            modifier = Modifier
        )
    }
}

@Preview
@LandscapePreview
@Composable
fun LibraryScreenErrorPreview() {
    UrgatTheme {
        LibraryScreen(
            uiState = LibraryUiState(
                todos = listOf(),
                todosFilteringUiInfo = TodoFilteringUiInfo(),
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
            navController = rememberNavController(),
            modifier = Modifier
        )
    }
}


@Preview
@Composable
fun LibraryContentPreview() {
    UrgatTheme {
        LibraryContent(
            headerItems = LIBRARY_HEADER_ITEMS,
            onHeaderItemClick = {},
            todos = SampleData.SAMPLE_TODOS.take(3),
            todoFilteringUiInfo = TodoFilteringUiInfo(),
            onSeeAllTodos = {},
            onTodoClick = {},
            onTodoCheckedChange = { _, _ -> },
            notes = SampleData.SAMPLE_NOTES.take(3),
            notesFilteringUiInfo = NotesFilteringUiInfo(),
            onSeeAllNotes = {},
            onNoteClick = {},
            listState = rememberLazyListState()
        )
    }
}

@Preview
@Composable
fun TodoCardPreview() {
    UrgatTheme {
        LazyColumn {
            itemTodo(
                todos = SampleData.SAMPLE_TODOS.take(3),
                todoFilteringUiInfo = TodoFilteringUiInfo(),
                onAddTodo = {},
                onSeeAllTodos = {},
                onTodoClick = {},
                onTodoCheckedChange = { _, _ -> }
            )
        }
    }
}

@Preview
@Composable
fun NotesCardPreview() {
    UrgatTheme {
        LazyColumn {
            itemNotes(
                notes = SampleData.SAMPLE_NOTES.take(3),
                notesFilteringUiInfo = NotesFilteringUiInfo(),
                onAddNote = {},
                onSeeAllNotes = {},
                onNoteClick = {}
            )
        }
    }
}