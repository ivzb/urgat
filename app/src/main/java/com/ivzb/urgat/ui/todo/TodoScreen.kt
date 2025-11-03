package com.ivzb.urgat.ui.todo

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.navigation.TodoDestination
import com.ivzb.urgat.ui.components.EmptyStateScreen
import com.ivzb.urgat.ui.components.FilterTodosMenu
import com.ivzb.urgat.ui.components.ItemHeader
import com.ivzb.urgat.ui.components.LandscapePreview
import com.ivzb.urgat.ui.components.LoadingState
import com.ivzb.urgat.ui.components.MoreTodosMenu
import com.ivzb.urgat.ui.components.UrgatScaffold
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun TodoScreen(
    onAddTodo: () -> Unit,
    onTodoClick: (Todo) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TodoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TodoScreenScaffold(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onAddTodo = onAddTodo,
        onTodoClick = onTodoClick,
        modifier = modifier
    )
}

@Composable
private fun TodoScreenScaffold(
    uiState: TodoUiState,
    onIntent: (TodoIntent) -> Unit,
    onAddTodo: () -> Unit,
    onTodoClick: (Todo) -> Unit,
    modifier: Modifier = Modifier
) {

    UrgatScaffold(
        selectedDestination = TodoDestination,
        onSearchQueryChanged = { query -> onIntent(TodoIntent.SearchQueryChanged(query)) },
        onAddItem = onAddTodo,
        withTopAppBarActions = uiState.items.isNotEmpty(),
        topAppBarActionsContent = {
            FilterTodosMenu(
                onFilterAllTodos = { onIntent(TodoIntent.SetFiltering(TodosFilterType.ALL_TODOS)) },
                onFilterActiveTodos = { onIntent(TodoIntent.SetFiltering(TodosFilterType.ACTIVE_TODOS)) },
                onFilterCompletedTodos = { onIntent(TodoIntent.SetFiltering(TodosFilterType.COMPLETED_TODOS)) },
            )

            MoreTodosMenu(
                onClearCompletedTodos = { onIntent(TodoIntent.ClearCompletedTodos) }
            )
        },
        modifier = modifier
    ) { listState ->

        when {
            uiState.isLoading -> {
                LoadingState(
                    label = uiState.filteringUiInfo.loadingLabel,
                )
            }

            !uiState.isLoading && uiState.items.isEmpty() -> {
                EmptyStateScreen(
                    label = uiState.filteringUiInfo.emptyLabel,
                    iconRes = uiState.filteringUiInfo.emptyIconRes
                )
            }

            uiState.hasError -> {
                EmptyStateScreen(
                    label = uiState.filteringUiInfo.errorLabel,
                    iconRes = uiState.filteringUiInfo.errorIconRes
                )
            }

            else -> {
                TodoContent(
                    todos = uiState.items,
                    currentFilteringLabel = uiState.filteringUiInfo.currentFilteringLabel,
                    onTodoClick = onTodoClick,
                    onTodoCheckedChange = { todo, completed -> onIntent(TodoIntent.CompleteTodo(todo, completed)) },
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

    LazyColumn (
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = dimensionResource(R.dimen.horizontal_margin),
            vertical = dimensionResource(R.dimen.vertical_margin)
        ),
        state = listState
    ) {

        item {
            ItemHeader(title = stringResource(currentFilteringLabel))
        }

        item {
            Spacer(modifier = Modifier
                .height(dimensionResource(R.dimen.vertical_margin))
            )
        }

        items(todos) { todo ->
            TodoItem(
                todo = todo,
                onTodoClick = onTodoClick,
                onCheckedChange = { onTodoCheckedChange(todo, it) }
            )
        }

        item {
            Spacer(modifier = Modifier
                .height(dimensionResource(R.dimen.vertical_margin))
            )
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
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shadowElevation = 2.dp
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .clickable { onTodoClick(todo) }
                .padding(dimensionResource(id = R.dimen.list_item_padding))
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
                        style = MaterialTheme.typography.bodyLarge,
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
                        style = MaterialTheme.typography.bodySmall,
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
                hasError = false
            ),
            onIntent = {},
            onAddTodo = {},
            onTodoClick = {},
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
                hasError = false
            ),
            onIntent = {},
            onAddTodo = {},
            onTodoClick = {},
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
                hasError = false
            ),
            onIntent = {},
            onAddTodo = {},
            onTodoClick = {},
            modifier = Modifier
        )
    }
}

@Preview
@LandscapePreview
@Composable
private fun TodoScreenScaffoldErrorPreview() {
    UrgatTheme {
        TodoScreenScaffold(
            uiState = TodoUiState(
                items = listOf(),
                filteringUiInfo = TodoFilteringUiInfo(),
                isLoading = false,
                hasError = true
            ),
            onIntent = {},
            onAddTodo = {},
            onTodoClick = {},
            modifier = Modifier
        )
    }
}

@Preview
@LandscapePreview
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
@LandscapePreview
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
@LandscapePreview
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
