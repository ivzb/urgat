package com.ivzb.urgat.ui.todo

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun TodoScreen(
    onAddTodo: () -> Unit,
    onTodoClick: (Todo) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TodoViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> {
            TodosNoContent(
                label = uiState.filteringUiInfo.loadingTodosLabel,
                iconRes = uiState.filteringUiInfo.loadingTodoIconRes,
                modifier = modifier
            )
        }

        !uiState.isLoading && uiState.items.isEmpty() -> {
            TodosNoContent(
                label = uiState.filteringUiInfo.noTodosLabel,
                iconRes = uiState.filteringUiInfo.noTodoIconRes,
                modifier = modifier
            )
        }

        uiState.hasError -> {
            TodosNoContent(
                label = uiState.filteringUiInfo.errorTodosLabel,
                iconRes = uiState.filteringUiInfo.errorTodoIconRes,
                modifier = modifier
            )
        }

        else -> {
            TodosContent(
                todos = uiState.items,
                currentFilteringLabel = uiState.filteringUiInfo.currentFilteringLabel,
                onTodoClick = onTodoClick,
                onTodoCheckedChange = viewModel::completeTodo,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun TodosContent(
    todos: List<Todo>,
    @StringRes currentFilteringLabel: Int,
    onTodoClick: (Todo) -> Unit,
    onTodoCheckedChange: (Todo, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(id = R.dimen.horizontal_margin))
    ) {
        Text(
            text = stringResource(currentFilteringLabel),
            modifier = Modifier.padding(
                horizontal = dimensionResource(id = R.dimen.list_item_padding),
                vertical = dimensionResource(id = R.dimen.vertical_margin)
            ),
            style = MaterialTheme.typography.headlineSmall
        )

        LazyColumn {
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
private fun TodoItem(
    todo: Todo,
    onCheckedChange: (Boolean) -> Unit,
    onTodoClick: (Todo) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(id = R.dimen.horizontal_margin),
                vertical = dimensionResource(id = R.dimen.list_item_padding),
            )
            .clickable { onTodoClick(todo) }
    ) {
        Checkbox(
            checked = todo.isCompleted,
            onCheckedChange = onCheckedChange
        )

        Text(
            text = todo.titleForList,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(
                start = dimensionResource(id = R.dimen.horizontal_margin)
            ),
            textDecoration = if (todo.isCompleted) {
                TextDecoration.LineThrough
            } else {
                null
            }
        )
    }
}

@Composable
private fun TodosNoContent(
    @StringRes label: Int,
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(96.dp)
        )
        Text(stringResource(id = label))
    }
}

@Preview
@Composable
private fun TodosContentPreview() {
    UrgatTheme {
        Surface {
            TodosContent(
                todos = listOf(
                    Todo(
                        title = "Title 1",
                        description = "Description 1",
                        isCompleted = false,
                        id = "ID 1"
                    ),
                    Todo(
                        title = "Title 2",
                        description = "Description 2",
                        isCompleted = true,
                        id = "ID 2"
                    ),
                    Todo(
                        title = "Title 3",
                        description = "Description 3",
                        isCompleted = true,
                        id = "ID 3"
                    ),
                    Todo(
                        title = "Title 4",
                        description = "Description 4",
                        isCompleted = false,
                        id = "ID 4"
                    ),
                    Todo(
                        title = "Title 5",
                        description = "Description 5",
                        isCompleted = true,
                        id = "ID 5"
                    ),
                ),
                currentFilteringLabel = R.string.all_todos,
                onTodoClick = { },
                onTodoCheckedChange = { _, _ -> },
            )
        }
    }
}

@Preview
@Composable
private fun EmptyTodosPreview() {
    UrgatTheme {
        Surface {
            TodosNoContent(
                label = R.string.no_todos,
                iconRes = R.drawable.logo_no_fill,
            )
        }
    }
}

@Preview
@Composable
private fun LoadingTodosPreview() {
    UrgatTheme {
        Surface {
            TodosNoContent(
                label = R.string.loading_todos,
                iconRes = R.drawable.ic_loading,
            )
        }
    }
}

@Preview
@Composable
private fun ErrorTodosPreview() {
    UrgatTheme {
        Surface {
            TodosNoContent(
                label = R.string.error_todos,
                iconRes = R.drawable.ic_error,
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
                    title = "Title",
                    description = "Description",
                    id = "ID"
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
                    title = "Title",
                    description = "Description",
                    isCompleted = true,
                    id = "ID"
                ),
                onTodoClick = { },
                onCheckedChange = { }
            )
        }
    }
}
