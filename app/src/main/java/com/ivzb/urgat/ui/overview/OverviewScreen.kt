package com.ivzb.urgat.ui.overview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.ui.components.EmptyState
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

    modifier: Modifier = Modifier,
    viewModel: OverviewViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> {
            EmptyState(
                label = uiState.filteringUiInfo.loadingLabel,
                iconRes = uiState.filteringUiInfo.loadingIconRes,
                modifier = modifier
            )
        }

        uiState.hasError -> {
            EmptyState(
                label = uiState.filteringUiInfo.errorLabel,
                iconRes = uiState.filteringUiInfo.errorIconRes,
                modifier = modifier
            )
        }

        else -> {
            OverviewContent(
                headerItems = uiState.headerItems,
                onHeaderItemClick = viewModel::onHeaderItemClick,
                todos = uiState.todoItems,
                todoFilteringUiInfo = uiState.todoFilteringUiInfo,
                onTodoClick = onTodoClick,
                onTodoCheckedChange = viewModel::completeTodo,
                onExpenseClick = onExpenseClick,
                onNoteClick = onNoteClick,
                modifier = modifier
            )
        }
    }

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
private fun OverviewContent(
    headerItems: List<HeaderItem>,
    todos: List<Todo>,
    todoFilteringUiInfo: TodoFilteringUiInfo,
    onHeaderItemClick: (HeaderItem) -> Unit,
    onTodoClick: (Todo) -> Unit,
    onTodoCheckedChange: (Todo, Boolean) -> Unit,
    onExpenseClick: (String) -> Unit,
    onNoteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val screenName = stringResource(R.string.overview)

    LazyColumn(
        modifier = modifier
            .padding(dimensionResource(R.dimen.content_padding))
            .semantics { contentDescription = screenName }
    ) {
        item {
            Header(
                items = headerItems,
                onItemClick = onHeaderItemClick
            )
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

//        ExpensesCard(
//            onSeeAllExpenses = onSeeAllExpenses,
//            onAddExpense = onAddExpense,
//            onExpenseClick = onExpenseClick
//        )
//
//        Spacer(
//            modifier = Modifier
//                .height(dimensionResource(R.dimen.list_item_padding))
//        )
//
//        NotesCard(
//            onSeeAllNotes = onSeeAllNotes,
//            onAddNote = onAddNote,
//            onNoteClick = onNoteClick
//        )
//
//        Spacer(
//            modifier = Modifier
//                .height(dimensionResource(R.dimen.list_item_padding))
//        )
//
//        WeatherCard(
//            onSeeAllWeather = onSeeAllWeather
//        )
    }
}

@Composable
private fun Header(
    items: List<HeaderItem>,
    onItemClick: (HeaderItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(stringResource(R.string.overview))

        Row {
            items.forEach {
                HeaderItem(
                    item = it,
                    onClick = onItemClick
                )
            }
        }
    }
}

@Composable
private fun HeaderItem(
    item: HeaderItem,
    onClick: (HeaderItem) -> Unit
) {
    Button(
        onClick = { onClick(item) }
    ) {
        Column {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
            )

            Text(stringResource(item.label))
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
                        horizontal = dimensionResource(id = R.dimen.list_item_padding),
                        vertical = dimensionResource(id = R.dimen.vertical_margin)
                    ),
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            items(todos) { todo ->
                TodoItem(
                    todo = todo,
                    onTodoClick = onTodoClick,
                    onCheckedChange = { onTodoCheckedChange(todo, it) },
                    withCheckbox = false,
                    modifier = Modifier
                        .padding(horizontal = dimensionResource(R.dimen.horizontal_margin))
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
        )
    }
}

@Preview
@Composable
fun OverviewHeader() {
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
        )
    }
}

@Preview
@Composable
fun OverviewHeaderItem() {
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
