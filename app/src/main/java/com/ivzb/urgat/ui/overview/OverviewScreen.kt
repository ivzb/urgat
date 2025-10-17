package com.ivzb.urgat.ui.overview

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ivzb.urgat.R
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.navigation.OverviewDestination
import com.ivzb.urgat.ui.components.EmptyState
import com.ivzb.urgat.ui.components.LandscapePreview
import com.ivzb.urgat.ui.components.MenuScaffold
import com.ivzb.urgat.ui.components.MoreTodosMenu
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.ui.todo.TodoFilteringUiInfo
import com.ivzb.urgat.ui.todo.TodoItem
import kotlinx.coroutines.flow.collectLatest

@Composable
fun OverviewScreen(
    onAddTodo: () -> Unit,
    onTodoClick: (Todo) -> Unit,

    onAddExpense: () -> Unit,
    onExpenseClick: (String) -> Unit,

    onAddNote: () -> Unit,
    onNoteClick: (String) -> Unit,

    navController: NavHostController,

    modifier: Modifier = Modifier,
    viewModel: OverviewViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    OverviewScreenScaffold(
        uiState = uiState,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onHeaderItemClick = viewModel::onHeaderItemClick,
        onTodoClick = onTodoClick,
        onExpenseClick = onExpenseClick,
        onNoteClick = onNoteClick,
        clearCompletedTodos = viewModel::clearCompletedTodos,
        completeTodo = viewModel::completeTodo,
        navController = navController,
        modifier = modifier
    )

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.AddTodo -> onAddTodo()
                is UiEvent.AddExpense -> onAddExpense()
                is UiEvent.AddNote -> onAddNote()
            }
        }
    }
}

@Composable
private fun OverviewScreenScaffold(
    uiState: OverviewUiState,
    onSearchQueryChanged: (String) -> Unit,
    onHeaderItemClick: (HeaderItem) -> Unit,
    onTodoClick: (Todo) -> Unit,
    onExpenseClick: (String) -> Unit,
    onNoteClick: (String) -> Unit,
    clearCompletedTodos: () -> Unit,
    completeTodo: (Todo, Boolean) -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    MenuScaffold(
        selectedDestination = OverviewDestination,
        onSearchQueryChanged = onSearchQueryChanged,
        navController = navController,
        withTopAppBarActions = uiState.todoItems.isNotEmpty(),
        topAppBarActionsContent = {
            MoreTodosMenu(
                onClearCompletedTodos = clearCompletedTodos
            )
        },
        modifier = modifier
    ) { listState ->

        when {
            uiState.isLoading -> {
                EmptyState(
                    label = uiState.filteringUiInfo.loadingLabel,
                    iconRes = uiState.filteringUiInfo.loadingIconRes,
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
                    onHeaderItemClick = onHeaderItemClick,
                    todos = uiState.todoItems,
                    todoFilteringUiInfo = uiState.todoFilteringUiInfo,
                    onTodoClick = onTodoClick,
                    onTodoCheckedChange = completeTodo,
                    onExpenseClick = onExpenseClick,
                    onNoteClick = onNoteClick,
                    listState = listState,
                )
            }
        }

    }
}

@Composable
private fun OverviewContent(
    headerItems: List<HeaderItem>,
    todos: List<Todo>,
    todoFilteringUiInfo: TodoFilteringUiInfo,
    onHeaderItemClick: (HeaderItem) -> Unit,
    onTodoClick: (Todo) -> Unit,
    onTodoCheckedChange: (Todo, Boolean) -> Unit,
    onExpenseClick: (String) -> Unit,
    onNoteClick: (String) -> Unit,
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
                onTodoClick = onTodoClick,
                onTodoCheckedChange = onTodoCheckedChange,
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

@Composable
private fun Header(
    items: List<HeaderItem>,
    onItemClick: (HeaderItem) -> Unit,
    orientation: Int,
    modifier: Modifier = Modifier
) {

    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.overview_header_item_padding)),
            modifier = modifier
                .fillMaxHeight()
                .clip(RoundedCornerShape(dimensionResource(R.dimen.menu_button_corner_radius)))
                .background(MaterialTheme.colorScheme.primary)
                .padding(dimensionResource(R.dimen.overview_header_item_padding))
        ) {

            HeaderContent(
                items = items,
                onItemClick = onItemClick,
                modifier = Modifier
                    .defaultMinSize(minWidth = dimensionResource(R.dimen.overview_header_item_min_width))
                    .weight(1f)
            )
        }
    } else {
        Row(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.overview_header_item_padding)),
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(dimensionResource(R.dimen.overview_header_item_padding))
        ) {

            HeaderContent(
                items = items,
                onItemClick = onItemClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun HeaderContent(
    items: List<HeaderItem>,
    onItemClick: (HeaderItem) -> Unit,
    modifier: Modifier = Modifier
) {

    items.forEach {
        HeaderItem(
            item = it,
            onClick = onItemClick,
            modifier = modifier
        )
    }
}

@Composable
private fun HeaderItem(
    item: HeaderItem,
    onClick: (HeaderItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { onClick(item) },
        contentPadding = PaddingValues(
            horizontal = 0.dp,
            vertical = dimensionResource(R.dimen.menu_content_padding)
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(dimensionResource(R.dimen.menu_button_corner_radius)),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                modifier = Modifier
                    .size(dimensionResource(R.dimen.menu_icon_size))
                    .padding(dimensionResource(R.dimen.menu_item_padding))
            )

            Text(
                text = stringResource(item.label),
                style = MaterialTheme.typography.labelMedium
                    .copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.menu_item_padding))
            )
        }
    }
}

private fun LazyListScope.itemTodo(
    todos: List<Todo>,
    todoFilteringUiInfo: TodoFilteringUiInfo,
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
                Text(
                    text = stringResource(todoFilteringUiInfo.currentFilteringLabel),
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(id = R.dimen.horizontal_margin),
                        vertical = dimensionResource(id = R.dimen.vertical_margin)
                    ),
                    style = MaterialTheme.typography.headlineSmall
                        .copy(fontWeight = FontWeight.Bold)
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

@Composable
private fun ExpensesCard(
    onSeeAllExpenses: () -> Unit,
    onAddExpense: () -> Unit,
    onExpenseClick: (String) -> Unit,
) {

    Text("Expenses card")
}

@Composable
private fun NotesCard(
    onSeeAllNotes: () -> Unit,
    onAddNote: () -> Unit,
    onNoteClick: (String) -> Unit,
) {

    Text("Notes card")
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
                    HeaderItem(
                        HeaderItemId.Todo,
                        Icons.Filled.Done,
                        R.string.add_todo
                    ),
                    HeaderItem(
                        HeaderItemId.Expense,
                        Icons.Filled.ShoppingCart,
                        R.string.add_expense
                    ),
                    HeaderItem(
                        HeaderItemId.Note,
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
            onSearchQueryChanged = {},
            onHeaderItemClick = {},
            onTodoClick = {},
            onExpenseClick = {},
            onNoteClick = {},
            clearCompletedTodos = {},
            completeTodo = { _, _ -> },
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
                    HeaderItem(
                        HeaderItemId.Todo,
                        Icons.Filled.Done,
                        R.string.add_todo
                    ),
                    HeaderItem(
                        HeaderItemId.Expense,
                        Icons.Filled.ShoppingCart,
                        R.string.add_expense
                    ),
                    HeaderItem(
                        HeaderItemId.Note,
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
            onSearchQueryChanged = {},
            onHeaderItemClick = {},
            onTodoClick = {},
            onExpenseClick = {},
            onNoteClick = {},
            clearCompletedTodos = {},
            completeTodo = { _, _ -> },
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
                    HeaderItem(
                        HeaderItemId.Todo,
                        Icons.Filled.Done,
                        R.string.add_todo
                    ),
                    HeaderItem(
                        HeaderItemId.Expense,
                        Icons.Filled.ShoppingCart,
                        R.string.add_expense
                    ),
                    HeaderItem(
                        HeaderItemId.Note,
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
            onSearchQueryChanged = {},
            onHeaderItemClick = {},
            onTodoClick = {},
            onExpenseClick = {},
            onNoteClick = {},
            clearCompletedTodos = {},
            completeTodo = { _, _ -> },
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
                    HeaderItem(
                        HeaderItemId.Todo,
                        Icons.Filled.Done,
                        R.string.add_todo
                    ),
                    HeaderItem(
                        HeaderItemId.Expense,
                        Icons.Filled.ShoppingCart,
                        R.string.add_expense
                    ),
                    HeaderItem(
                        HeaderItemId.Note,
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
            onSearchQueryChanged = {},
            onHeaderItemClick = {},
            onTodoClick = {},
            onExpenseClick = {},
            onNoteClick = {},
            clearCompletedTodos = {},
            completeTodo = { _, _ -> },
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
                    HeaderItemId.Todo,
                    Icons.Filled.Done,
                    R.string.add_todo
                ),
                HeaderItem(
                    HeaderItemId.Expense,
                    Icons.Filled.ShoppingCart,
                    R.string.add_expense
                ),
                HeaderItem(
                    HeaderItemId.Note,
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
            onTodoClick = {},
            onTodoCheckedChange = { _, _ -> },
            onExpenseClick = {},
            onNoteClick = {},
            listState = rememberLazyListState()
        )
    }
}

@Preview
@Composable
fun OverviewHeaderPreview() {
    UrgatTheme {
        Header(
            items = listOf(
                HeaderItem(
                    HeaderItemId.Todo,
                    Icons.Filled.Done,
                    R.string.add_todo
                ),
                HeaderItem(
                    HeaderItemId.Expense,
                    Icons.Filled.ShoppingCart,
                    R.string.add_expense
                ),
                HeaderItem(
                    HeaderItemId.Note,
                    Icons.Filled.Person,
                    R.string.add_note
                ),
            ),
            onItemClick = {},
            orientation = Configuration.ORIENTATION_PORTRAIT
        )
    }
}

@LandscapePreview
@Composable
fun OverviewHeaderLandscapePreview() {
    UrgatTheme {
        Header(
            items = listOf(
                HeaderItem(
                    HeaderItemId.Todo,
                    Icons.Filled.Done,
                    R.string.add_todo
                ),
                HeaderItem(
                    HeaderItemId.Expense,
                    Icons.Filled.ShoppingCart,
                    R.string.add_expense
                ),
                HeaderItem(
                    HeaderItemId.Note,
                    Icons.Filled.Person,
                    R.string.add_note
                ),
            ),
            onItemClick = {},
            orientation = Configuration.ORIENTATION_LANDSCAPE
        )
    }
}

@Preview
@Composable
fun OverviewHeaderItemPreview() {
    UrgatTheme {
        HeaderItem(
            item = HeaderItem(
                HeaderItemId.Todo,
                Icons.Filled.Done,
                R.string.add_todo
            ),
            onClick = {},
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
        ExpensesCard(
            onSeeAllExpenses = {},
            onAddExpense = {},
            onExpenseClick = {}
        )
    }
}

@Preview
@Composable
fun NotesCardPreview() {
    UrgatTheme {
        NotesCard(
            onSeeAllNotes = {},
            onAddNote = {},
            onNoteClick = {}
        )
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
