package com.ivzb.urgat.ui.todo

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ivzb.urgat.R
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.navigation.TodoDestination
import com.ivzb.urgat.ui.components.EmptyState
import com.ivzb.urgat.ui.components.FilterTodosMenu
import com.ivzb.urgat.ui.components.LandscapePreview
import com.ivzb.urgat.ui.components.MenuScaffold
import com.ivzb.urgat.ui.components.MoreTodosMenu
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun TodoScreen(
    onAddTodo: () -> Unit,
    onTodoClick: (Todo) -> Unit,

    navController: NavHostController,

    modifier: Modifier = Modifier,
    viewModel: TodoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TodoScreenScaffold(
        uiState = uiState,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onAddTodo = onAddTodo,
        onTodoClick = onTodoClick,
        setFiltering = viewModel::setFiltering,
        clearCompletedTodos = viewModel::clearCompletedTodos,
        completeTodo = viewModel::completeTodo,
        navController = navController,
        modifier = modifier
    )
}

@Composable
private fun TodoScreenScaffold(
    uiState: TodoUiState,
    onSearchQueryChanged: (String) -> Unit,
    onAddTodo: () -> Unit,
    onTodoClick: (Todo) -> Unit,
    setFiltering: (TodosFilterType) -> Unit,
    clearCompletedTodos: () -> Unit,
    completeTodo: (Todo, Boolean) -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    MenuScaffold(
        selectedDestination = TodoDestination,
        onSearchQueryChanged = onSearchQueryChanged,
        onAddItem = onAddTodo,
        navController = navController,
        withTopAppBarActions = uiState.items.isNotEmpty(),
        topAppBarActionsContent = {
            FilterTodosMenu(
                onFilterAllTodos = { setFiltering(TodosFilterType.ALL_TODOS) },
                onFilterActiveTodos = { setFiltering(TodosFilterType.ACTIVE_TODOS) },
                onFilterCompletedTodos = { setFiltering(TodosFilterType.COMPLETED_TODOS) },
            )

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
                    iconRes = uiState.filteringUiInfo.loadingIconRes
                )
            }

            !uiState.isLoading && uiState.items.isEmpty() -> {
                EmptyState(
                    label = uiState.filteringUiInfo.emptyLabel,
                    iconRes = uiState.filteringUiInfo.emptyIconRes
                )
            }

            uiState.hasError -> {
                EmptyState(
                    label = uiState.filteringUiInfo.errorLabel,
                    iconRes = uiState.filteringUiInfo.errorIconRes
                )
            }

            else -> {
                TodoContent(
                    todos = uiState.items,
                    currentFilteringLabel = uiState.filteringUiInfo.currentFilteringLabel,
                    onTodoClick = onTodoClick,
                    onTodoCheckedChange = completeTodo,
                    listState = listState
                )
            }
        }
    }
}

@Composable
fun TodoContent(
    todos: List<Todo>,
    @StringRes currentFilteringLabel: Int,
    onTodoClick: (Todo) -> Unit,
    onTodoCheckedChange: (Todo, Boolean) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Text(
            text = stringResource(currentFilteringLabel),
            modifier = Modifier.padding(
                horizontal = dimensionResource(id = R.dimen.horizontal_margin),
                vertical = dimensionResource(id = R.dimen.vertical_margin)
            ),
            style = MaterialTheme.typography.headlineSmall
                .copy(fontWeight = FontWeight.Bold)
        )

        LazyColumn (
            modifier = modifier
                .fillMaxSize(),
            state = listState
        ) {
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
fun TodoItem(
    todo: Todo,
    onCheckedChange: (Boolean) -> Unit,
    onTodoClick: (Todo) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTodoClick(todo) }
            .padding(dimensionResource(id = R.dimen.list_item_padding)),
    ) {
        Checkbox(
            checked = todo.isCompleted,
            onCheckedChange = onCheckedChange,
            modifier = Modifier
                .align(Alignment.Top)
        )

        Column {
            if (todo.title.isNotEmpty()) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (todo.isCompleted) {
                        TextDecoration.LineThrough
                    } else {
                        null
                    }
                )
            }

            if (todo.description.isNotEmpty()) {
                Text(
                    text = todo.description,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (todo.isCompleted) {
                        TextDecoration.LineThrough
                    } else {
                        null
                    }
                )
            }
        }
    }
}

@Preview
@LandscapePreview
@Composable
private fun TodoScreenScaffoldPreview() {
    UrgatTheme {
        TodoScreenScaffold(
            uiState = TodoUiState(
                items = listOf(
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
                    Todo(
                        id = "ID 4",
                        title = "Title 4",
                        description = "Description 4",
                        isCompleted = false,
                        createdAt = 3
                    ),
                    Todo(
                        id = "ID 5",
                        title = "Title 5",
                        description = "Description 5",
                        isCompleted = true,
                        createdAt = 4
                    ),
                    Todo(
                        id = "ID 6",
                        title = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
                        description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
                        isCompleted = false,
                        createdAt = 5
                    ),
                    Todo(
                        id = "ID 7",
                        title = "Test",
                        description = "",
                        isCompleted = false,
                        createdAt = 6
                    ),
                    Todo(
                        id = "ID 8",
                        title = "",
                        description = "Test",
                        isCompleted = false,
                        createdAt = 7
                    ),
                ),
                filteringUiInfo = TodoFilteringUiInfo(),
                isLoading = false,
            ),
            onSearchQueryChanged = {},
            onAddTodo = {},
            onTodoClick = {},
            setFiltering = {},
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
private fun TodoScreenScaffoldEmptyPreview() {
    UrgatTheme {
        TodoScreenScaffold(
            uiState = TodoUiState(
                items = listOf(),
                filteringUiInfo = TodoFilteringUiInfo(),
                isLoading = false,
            ),
            onSearchQueryChanged = {},
            onAddTodo = {},
            onTodoClick = {},
            setFiltering = {},
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
private fun TodoScreenScaffoldLoadingPreview() {
    UrgatTheme {
        TodoScreenScaffold(
            uiState = TodoUiState(
                items = listOf(),
                filteringUiInfo = TodoFilteringUiInfo(),
                isLoading = true,
            ),
            onSearchQueryChanged = {},
            onAddTodo = {},
            onTodoClick = {},
            setFiltering = {},
            clearCompletedTodos = {},
            completeTodo = { _, _ -> },
            navController = rememberNavController(),
            modifier = Modifier
        )
    }
}

@Preview
@Composable
private fun TodoContentPreview() {
    UrgatTheme {
        Surface {
            TodoContent(
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
                    Todo(
                        id = "ID 4",
                        title = "Title 4",
                        description = "Description 4",
                        isCompleted = false,
                        createdAt = 3
                    ),
                    Todo(
                        id = "ID 5",
                        title = "Title 5",
                        description = "Description 5",
                        isCompleted = true,
                        createdAt = 4
                    ),
                    Todo(
                        id = "ID 6",
                        title = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
                        description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
                        isCompleted = false,
                        createdAt = 5
                    ),
                    Todo(
                        id = "ID 7",
                        title = "Test",
                        description = "",
                        isCompleted = false,
                        createdAt = 6
                    ),
                    Todo(
                        id = "ID 8",
                        title = "",
                        description = "Test",
                        isCompleted = false,
                        createdAt = 7
                    ),
                ),
                currentFilteringLabel = R.string.all_todos,
                onTodoClick = { },
                onTodoCheckedChange = { _, _ -> },
                listState = rememberLazyListState()
            )
        }
    }
}

@Preview
@Composable
private fun TodoItemPreview() {
    UrgatTheme {
        Surface {
            TodoItem(
                todo = Todo(
                    id = "ID",
                    title = "Title",
                    description = "Description",
                    createdAt = 0
                ),
                onTodoClick = { },
                onCheckedChange = { }
            )
        }
    }
}

@Preview
@Composable
private fun TodoItemCompletedPreview() {
    UrgatTheme {
        Surface {
            TodoItem(
                todo = Todo(
                    id = "ID",
                    title = "Title",
                    description = "Description",
                    isCompleted = true,
                    createdAt = 0
                ),
                onTodoClick = { },
                onCheckedChange = { }
            )
        }
    }
}
