package com.ivzb.urgat.ui.overview

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Text
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
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.navigation.OverviewDestination
import com.ivzb.urgat.ui.components.EmptyState
import com.ivzb.urgat.ui.components.Header
import com.ivzb.urgat.ui.components.HeaderItem
import com.ivzb.urgat.ui.components.ItemHeaderWithSeeAll
import com.ivzb.urgat.ui.components.LandscapePreview
import com.ivzb.urgat.ui.components.LoadingState
import com.ivzb.urgat.ui.components.MenuScaffold
import com.ivzb.urgat.ui.components.MoreTodosMenu
import com.ivzb.urgat.ui.expenses.ExpenseItem
import com.ivzb.urgat.ui.expenses.ExpensesFilteringUiInfo
import com.ivzb.urgat.ui.notes.NoteItem
import com.ivzb.urgat.ui.notes.NotesFilteringUiInfo
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.ui.todo.TodoFilteringUiInfo
import com.ivzb.urgat.ui.todo.TodoItem
import kotlinx.coroutines.flow.collectLatest

@Composable
fun OverviewScreen(
    onAddTodo: () -> Unit,
    onSeeAllTodos: () -> Unit,
    onTodoClick: (Todo) -> Unit,

    onAddExpense: () -> Unit,
    onSeeAllExpenses: () -> Unit,
    onExpenseClick: (Expense) -> Unit,

    onAddNote: () -> Unit,
    onSeeAllNotes: () -> Unit,
    onNoteClick: (Note) -> Unit,

    navController: NavHostController,

    modifier: Modifier = Modifier,
    viewModel: OverviewViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    OverviewScreenScaffold(
        uiState = uiState,
        onIntent = viewModel::onIntent,

        onSeeAllTodos = onSeeAllTodos,
        onTodoClick = onTodoClick,

        onSeeAllExpenses = onSeeAllExpenses,
        onExpenseClick = onExpenseClick,

        onSeeAllNotes = onSeeAllNotes,
        onNoteClick = onNoteClick,

        navController = navController,
        modifier = modifier
    )

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is OverviewUiEvent.AddTodo -> onAddTodo()
                is OverviewUiEvent.AddExpense -> onAddExpense()
                is OverviewUiEvent.AddNote -> onAddNote()
            }
        }
    }
}

@Composable
private fun OverviewScreenScaffold(
    uiState: OverviewUiState,
    onIntent: (OverviewIntent) -> Unit,

    onSeeAllTodos: () -> Unit,
    onTodoClick: (Todo) -> Unit,

    onSeeAllExpenses: () -> Unit,
    onExpenseClick: (Expense) -> Unit,

    onSeeAllNotes: () -> Unit,
    onNoteClick: (Note) -> Unit,

    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    MenuScaffold(
        selectedDestination = OverviewDestination,
        onSearchQueryChanged = { query -> onIntent(OverviewIntent.SearchQueryChanged(query)) },
        navController = navController,
        withTopAppBarActions = uiState.todoItems.isNotEmpty(),
        topAppBarActionsContent = {
            MoreTodosMenu(
                onClearCompletedTodos = { onIntent(OverviewIntent.ClearCompletedTodos) }
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
                EmptyState(
                    label = uiState.filteringUiInfo.errorLabel,
                    iconRes = uiState.filteringUiInfo.errorIconRes,
                )
            }

            else -> {
                OverviewContent(
                    headerItems = uiState.headerItems,
                    onHeaderItemClick = { item -> onIntent(OverviewIntent.HeaderItemClick(item)) },

                    todos = uiState.todoItems,
                    todoFilteringUiInfo = uiState.todoFilteringUiInfo,
                    onSeeAllTodos = onSeeAllTodos,
                    onTodoClick = onTodoClick,
                    onTodoCheckedChange = { todo, completed -> onIntent(OverviewIntent.CompleteTodo(todo, completed)) },

                    expenses = uiState.expensesItems,
                    expensesFilteringUiInfo = uiState.expensesFilteringUiInfo,
                    onSeeAllExpenses = onSeeAllExpenses,
                    onExpenseClick = onExpenseClick,

                    notes = uiState.notesItems,
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
private fun OverviewContent(
    headerItems: List<OverviewHeaderItem>,
    onHeaderItemClick: (OverviewHeaderItem) -> Unit,

    todos: List<Todo>,
    todoFilteringUiInfo: TodoFilteringUiInfo,
    onSeeAllTodos: () -> Unit,
    onTodoClick: (Todo) -> Unit,
    onTodoCheckedChange: (Todo, Boolean) -> Unit,

    expenses: List<Expense>,
    expensesFilteringUiInfo: ExpensesFilteringUiInfo,
    onSeeAllExpenses: () -> Unit,
    onExpenseClick: (Expense) -> Unit,

    notes: List<Note>,
    notesFilteringUiInfo: NotesFilteringUiInfo,
    onSeeAllNotes: () -> Unit,
    onNoteClick: (Note) -> Unit,

    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    val screenName = stringResource(R.string.overview)
    val configuration = LocalConfiguration.current
    val orientation = configuration.orientation

    Row (
        modifier = Modifier.fillMaxSize()
    ) {

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Header(
                items = headerItems,
                onItemClick = onHeaderItemClick,
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
                        onItemClick = onHeaderItemClick,
                        orientation
                    )
                }
            }

            itemTodo(
                todos = todos,
                todoFilteringUiInfo = todoFilteringUiInfo,
                onSeeAllTodos = onSeeAllTodos,
                onTodoClick = onTodoClick,
                onTodoCheckedChange = onTodoCheckedChange,
            )

            item {
                Spacer(
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.list_item_padding))
                )
            }

            itemExpenses(
                expenses = expenses,
                expensesFilteringUiInfo = expensesFilteringUiInfo,
                onSeeAllExpenses = onSeeAllExpenses,
                onExpenseClick = onExpenseClick
            )

            item {
                Spacer(
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.list_item_padding))
                )
            }

            itemNotes(
                notes = notes,
                notesFilteringUiInfo = notesFilteringUiInfo,
                onSeeAllNotes = onSeeAllNotes,
                onNoteClick = onNoteClick
            )

            item {
                Spacer(
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.list_item_padding))
                )
            }
        }
    }
}

private fun LazyListScope.itemTodo(
    todos: List<Todo>,
    todoFilteringUiInfo: TodoFilteringUiInfo,
    onSeeAllTodos: () -> Unit,
    onTodoClick: (Todo) -> Unit,
    onTodoCheckedChange: (Todo, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        todos.isEmpty() -> {
            item {
                EmptyState(
                    label = todoFilteringUiInfo.emptyLabel,
                    iconRes = todoFilteringUiInfo.emptyIconRes,
                    modifier = modifier
                )
            }
        }

        else -> {
            item {
                ItemHeaderWithSeeAll(
                    title = stringResource(todoFilteringUiInfo.currentFilteringLabel),
                    onSeeAllClick = onSeeAllTodos,
                    modifier = Modifier.padding(
                        vertical = dimensionResource(id = R.dimen.vertical_margin)
                    ),
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

private fun LazyListScope.itemExpenses(
    expenses: List<Expense>,
    expensesFilteringUiInfo: ExpensesFilteringUiInfo,
    onSeeAllExpenses: () -> Unit,
    onExpenseClick: (Expense) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        expenses.isEmpty() -> {
            item {
                EmptyState(
                    label = expensesFilteringUiInfo.emptyLabel,
                    iconRes = expensesFilteringUiInfo.emptyIconRes,
                    modifier = modifier
                )
            }
        }

        else -> {
            item {
                ItemHeaderWithSeeAll(
                    title = stringResource(expensesFilteringUiInfo.currentFilteringLabel),
                    onSeeAllClick = onSeeAllExpenses,
                    modifier = Modifier.padding(
                        vertical = dimensionResource(id = R.dimen.vertical_margin)
                    ),
                )
            }

            items(expenses) { expense ->
                ExpenseItem(
                    expense = expense,
                    onExpenseClick = onExpenseClick
                )
            }
        }
    }
}

private fun LazyListScope.itemNotes(
    notes: List<Note>,
    notesFilteringUiInfo: NotesFilteringUiInfo,
    onSeeAllNotes: () -> Unit,
    onNoteClick: (Note) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        notes.isEmpty() -> {
            item {
                EmptyState(
                    label = notesFilteringUiInfo.emptyLabel,
                    iconRes = notesFilteringUiInfo.emptyIconRes,
                    modifier = modifier
                )
            }
        }

        else -> {
            item {
                ItemHeaderWithSeeAll(
                    title = stringResource(notesFilteringUiInfo.currentFilteringLabel),
                    onSeeAllClick = onSeeAllNotes,
                    modifier = Modifier.padding(
                        vertical = dimensionResource(id = R.dimen.vertical_margin)
                    ),
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

@Composable
private fun WeatherCard(
    onSeeAllWeather: () -> Unit
) {

    Text("Weather card")
}

@Preview
@LandscapePreview
@Composable
fun OverviewScreenScaffoldTodosPreview() {
    UrgatTheme {
        OverviewScreenScaffold(
            uiState = OverviewUiState(
                headerItems = listOf(
                    OverviewHeaderItem(
                        OverviewHeaderItemId.Todo,
                        Icons.Filled.Done,
                        R.string.add_todo
                    ),
                    OverviewHeaderItem(
                        OverviewHeaderItemId.Expense,
                        Icons.Filled.ShoppingCart,
                        R.string.add_expense
                    ),
                    OverviewHeaderItem(
                        OverviewHeaderItemId.Note,
                        Icons.Filled.Person,
                        R.string.add_note
                    ),
                ),
                todoItems = listOf(
                    Todo(
                        id = "ID 1",
                        title = "Title 1",
                        description = "Description 1",
                        isCompleted = false,
                        createdAt = 0
                    ),
                    Todo(
                        id = "ID 2",
                        title = "Title 2",
                        description = "Description 2",
                        isCompleted = true,
                        createdAt = 1
                    ),
                    Todo(
                        id = "ID 3",
                        title = "Title 3",
                        description = "Description 3",
                        isCompleted = true,
                        createdAt = 2
                    ),
                ),
                todoFilteringUiInfo = TodoFilteringUiInfo(),
                isLoading = false,
                hasError = false,
                filteringUiInfo = OverviewFilteringUiInfo()
            ),
            onIntent = {},
            onSeeAllTodos = {},
            onTodoClick = {},
            onSeeAllExpenses = {},
            onExpenseClick = {},
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
fun OverviewScreenScaffoldEmptyTodosPreview() {
    UrgatTheme {
        OverviewScreenScaffold(
            uiState = OverviewUiState(
                headerItems = listOf(
                    OverviewHeaderItem(
                        OverviewHeaderItemId.Todo,
                        Icons.Filled.Done,
                        R.string.add_todo
                    ),
                    OverviewHeaderItem(
                        OverviewHeaderItemId.Expense,
                        Icons.Filled.ShoppingCart,
                        R.string.add_expense
                    ),
                    OverviewHeaderItem(
                        OverviewHeaderItemId.Note,
                        Icons.Filled.Person,
                        R.string.add_note
                    ),
                ),
                todoItems = listOf(),
                todoFilteringUiInfo = TodoFilteringUiInfo(),
                isLoading = false,
                hasError = false,
                filteringUiInfo = OverviewFilteringUiInfo()
            ),
            onIntent = {},
            onSeeAllTodos = {},
            onTodoClick = {},
            onSeeAllExpenses = {},
            onExpenseClick = {},
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
fun OverviewScreenScaffoldLoadingPreview() {
    UrgatTheme {
        OverviewScreenScaffold(
            uiState = OverviewUiState(
                headerItems = listOf(
                    OverviewHeaderItem(
                        OverviewHeaderItemId.Todo,
                        Icons.Filled.Done,
                        R.string.add_todo
                    ),
                    OverviewHeaderItem(
                        OverviewHeaderItemId.Expense,
                        Icons.Filled.ShoppingCart,
                        R.string.add_expense
                    ),
                    OverviewHeaderItem(
                        OverviewHeaderItemId.Note,
                        Icons.Filled.Person,
                        R.string.add_note
                    ),
                ),
                todoItems = listOf(),
                todoFilteringUiInfo = TodoFilteringUiInfo(),
                isLoading = true,
                hasError = false,
                filteringUiInfo = OverviewFilteringUiInfo()
            ),
            onIntent = {},
            onSeeAllTodos = {},
            onTodoClick = {},
            onSeeAllExpenses = {},
            onExpenseClick = {},
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
fun OverviewScreenScaffoldErrorPreview() {
    UrgatTheme {
        OverviewScreenScaffold(
            uiState = OverviewUiState(
                headerItems = listOf(
                    OverviewHeaderItem(
                        OverviewHeaderItemId.Todo,
                        Icons.Filled.Done,
                        R.string.add_todo
                    ),
                    OverviewHeaderItem(
                        OverviewHeaderItemId.Expense,
                        Icons.Filled.ShoppingCart,
                        R.string.add_expense
                    ),
                    OverviewHeaderItem(
                        OverviewHeaderItemId.Note,
                        Icons.Filled.Person,
                        R.string.add_note
                    ),
                ),
                todoItems = listOf(),
                todoFilteringUiInfo = TodoFilteringUiInfo(),
                isLoading = false,
                hasError = true,
                filteringUiInfo = OverviewFilteringUiInfo()
            ),
            onIntent = {},
            onSeeAllTodos = {},
            onTodoClick = {},
            onSeeAllExpenses = {},
            onExpenseClick = {},
            onSeeAllNotes = {},
            onNoteClick = {},
            navController = rememberNavController(),
            modifier = Modifier
        )
    }
}


@Preview
@Composable
fun OverviewContentPreview() {
    UrgatTheme {
        OverviewContent(
            headerItems = listOf(
                HeaderItem(
                    OverviewHeaderItemId.Todo,
                    Icons.Filled.Done,
                    R.string.add_todo
                ),
                HeaderItem(
                    OverviewHeaderItemId.Expense,
                    Icons.Filled.ShoppingCart,
                    R.string.add_expense
                ),
                HeaderItem(
                    OverviewHeaderItemId.Note,
                    Icons.Filled.Person,
                    R.string.add_note
                ),
            ),
            onHeaderItemClick = {},
            todos = listOf(
                Todo(
                    id = "ID 1",
                    title = "Title 1",
                    description = "Description 1",
                    isCompleted = false,
                    createdAt = 0
                ),
                Todo(
                    id = "ID 2",
                    title = "Title 2",
                    description = "Description 2",
                    isCompleted = true,
                    createdAt = 1
                ),
                Todo(
                    id = "ID 3",
                    title = "Title 3",
                    description = "Description 3",
                    isCompleted = true,
                    createdAt = 2
                ),
            ),
            todoFilteringUiInfo = TodoFilteringUiInfo(),
            onSeeAllTodos = {},
            onTodoClick = {},
            onTodoCheckedChange = { _, _ -> },
            expenses =  listOf(
                Expense(
                    id = "ID 1",
                    name = "Food",
                    amount = 10.toBigDecimal(),
                    category = "food",
                    date = 1761335417,
                    isDeleted = false,
                    createdAt = 0,
                ),
                Expense(
                    id = "ID 2",
                    name = "Stuff",
                    amount = 23.23.toBigDecimal(),
                    category = "transport",
                    date = 1761335417,
                    isDeleted = false,
                    createdAt = 0,
                ),
                Expense(
                    id = "ID 3",
                    name = "Salary",
                    amount = -920.toBigDecimal(),
                    category = "income",
                    date = 1761335417,
                    isDeleted = false,
                    createdAt = 0,
                )
            ),
            expensesFilteringUiInfo = ExpensesFilteringUiInfo(),
            onSeeAllExpenses = {},
            onExpenseClick = {},
            notes = listOf(
                Note(
                    id = "ID 1",
                    content = "Remember to buy milk and eggs before the store closes.",
                    isDeleted = false,
                    createdAt = 0,
                ),
                Note(
                    id = "ID 2",
                    content = "Meeting notes: Discuss project milestones, finalize design specs, and assign tasks for the next sprint. Don’t forget to update the documentation.",
                    isDeleted = false,
                    createdAt = 0,
                ),
                Note(
                    id = "ID 3",
                    content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed euismod, nunc vel tincidunt egestas, justo nulla facilisis mauris, nec rhoncus erat massa vel urna. Curabitur id sagittis mi, in viverra libero. Suspendisse potenti.",
                    isDeleted = false,
                    createdAt = 0,
                ),
                Note(
                    id = "ID 4",
                    content = "Call the electrician tomorrow morning about the kitchen lights — mention the flickering issue.",
                    isDeleted = false,
                    createdAt = 0,
                ),
                Note(
                    id = "ID 5",
                    content = "Books to read: Atomic Habits, Deep Work, and The Pragmatic Programmer.",
                    isDeleted = false,
                    createdAt = 0,
                )
            ),
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
                todos = listOf(
                    Todo(
                        id = "ID 1",
                        title = "Title 1",
                        description = "Description 1",
                        isCompleted = false,
                        createdAt = 0
                    ),
                    Todo(
                        id = "ID 2",
                        title = "Title 2",
                        description = "Description 2",
                        isCompleted = true,
                        createdAt = 1
                    ),
                    Todo(
                        id = "ID 3",
                        title = "Title 3",
                        description = "Description 3",
                        isCompleted = true,
                        createdAt = 2
                    ),
                ),
                todoFilteringUiInfo = TodoFilteringUiInfo(),
                onSeeAllTodos = {},
                onTodoClick = {},
                onTodoCheckedChange = { _, _ -> }
            )
        }
    }
}

@Preview
@Composable
fun ExpensesCardPreview() {
    UrgatTheme {
        LazyColumn {
            itemExpenses(
                expenses = listOf(
                    Expense(
                        id = "ID 1",
                        name = "Food",
                        amount = 10.toBigDecimal(),
                        category = "food",
                        date = 1761335417,
                        isDeleted = false,
                        createdAt = 0,
                    ),
                    Expense(
                        id = "ID 2",
                        name = "Stuff",
                        amount = 23.23.toBigDecimal(),
                        category = "transport",
                        date = 1761335417,
                        isDeleted = false,
                        createdAt = 0,
                    ),
                    Expense(
                        id = "ID 3",
                        name = "Salary",
                        amount = -920.toBigDecimal(),
                        category = "income",
                        date = 1761335417,
                        isDeleted = false,
                        createdAt = 0,
                    )
                ),
                expensesFilteringUiInfo = ExpensesFilteringUiInfo(),
                onSeeAllExpenses = {},
                onExpenseClick = {}
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
                notes = listOf(
                    Note(
                        id = "ID 1",
                        content = "Remember to buy milk and eggs before the store closes.",
                        isDeleted = false,
                        createdAt = 0,
                    ),
                    Note(
                        id = "ID 2",
                        content = "Meeting notes: Discuss project milestones, finalize design specs, and assign tasks for the next sprint. Don’t forget to update the documentation.",
                        isDeleted = false,
                        createdAt = 0,
                    ),
                    Note(
                        id = "ID 3",
                        content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed euismod, nunc vel tincidunt egestas, justo nulla facilisis mauris, nec rhoncus erat massa vel urna. Curabitur id sagittis mi, in viverra libero. Suspendisse potenti.",
                        isDeleted = false,
                        createdAt = 0,
                    ),
                    Note(
                        id = "ID 4",
                        content = "Call the electrician tomorrow morning about the kitchen lights — mention the flickering issue.",
                        isDeleted = false,
                        createdAt = 0,
                    ),
                    Note(
                        id = "ID 5",
                        content = "Books to read: Atomic Habits, Deep Work, and The Pragmatic Programmer.",
                        isDeleted = false,
                        createdAt = 0,
                    )
                ),
                notesFilteringUiInfo = NotesFilteringUiInfo(),
                onSeeAllNotes = {},
                onNoteClick = {}
            )
        }
    }
}

@Preview
@Composable
fun WeatherCardPreview() {
    UrgatTheme {
        WeatherCard(
            onSeeAllWeather = {}
        )
    }
}
