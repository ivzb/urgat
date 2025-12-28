package com.ivzb.urgat.ui.todos

import androidx.annotation.VisibleForTesting
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.EmptyStateScreen
import com.ivzb.urgat.ui.components.FilterMenu
import com.ivzb.urgat.ui.components.ItemHeaderLarge
import com.ivzb.urgat.ui.components.ListDetailsScaffold
import com.ivzb.urgat.ui.components.LoadingState
import com.ivzb.urgat.ui.components.MoreTodosMenu
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun TodosScreen(
    viewModel: TodosViewModel,

    onAddTodo: () -> Unit,
    onTodoClick: (Todo) -> Unit,
    onBack: () -> Unit,

    modifier: Modifier = Modifier,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TodosScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onAddTodo = onAddTodo,
        onTodoClick = onTodoClick,
        onBack = onBack,
        modifier = modifier
    )
}

@VisibleForTesting
@Composable
fun TodosScreen(
    uiState: TodosUiState,
    onIntent: (TodosIntent) -> Unit,
    onAddTodo: () -> Unit,
    onTodoClick: (Todo) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = remember { LazyListState() }

    ListDetailsScaffold(
        selectedRoute = TodosRoute,
        onSearchQueryChanged = { query -> onIntent(TodosIntent.SearchQueryChanged(query)) },
        onAddItem = onAddTodo,
        onBack = onBack,
        topAppBarActionsContent = {
            FilterMenu(
                items = uiState.filteringUiInfo.filterMenuItems,
                onFilter = { onIntent(TodosIntent.SetFiltering(it)) },
            )

            MoreTodosMenu(
                onClearCompletedTodos = { onIntent(TodosIntent.ClearCompletedTodos) }
            )
        },
        listState = listState,
        modifier = modifier
    ) {

        when {
            uiState.isLoading -> {
                LoadingState(
                    label = uiState.filteringUiInfo.loadingLabel,
                )
            }

            uiState.hasError -> {
                EmptyStateScreen(
                    label = uiState.filteringUiInfo.errorLabel,
                    iconRes = uiState.filteringUiInfo.errorIconRes
                )
            }

            uiState.items.isEmpty() -> {
                EmptyStateScreen(
                    label = uiState.filteringUiInfo.emptyLabel,
                    iconRes = uiState.filteringUiInfo.emptyIconRes
                )
            }

            else -> {
                TodosContent(
                    todos = uiState.items,
                    onTodoClick = onTodoClick,
                    onTodoCheckedChange = { todo, completed -> onIntent(TodosIntent.CompleteTodos(todo, completed)) },
                    filteringUiInfo = uiState.filteringUiInfo,
                    listState = listState
                )
            }
        }
    }
}

@Composable
fun TodosContent(
    todos: List<Todo>,
    onTodoClick: (Todo) -> Unit,
    onTodoCheckedChange: (Todo, Boolean) -> Unit,
    filteringUiInfo: TodosFilteringUiInfo,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {

    LazyColumn (
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = dimensionResource(R.dimen.content_padding_horizontal),
            vertical = dimensionResource(R.dimen.content_padding_vertical),
        ),
        state = listState
    ) {

        item {
            ItemHeaderLarge(text = stringResource(filteringUiInfo.currentFilteringLabel))
        }

        item {
            Spacer(modifier = Modifier
                .height(dimensionResource(R.dimen.vertical_margin))
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

@AllDevicesPreview
@Composable
private fun TodosScreenPreview() {
    UrgatTheme {
        TodosScreen(
            uiState = TodosUiState(
                items = SampleData.SAMPLE_TODOS,
                filteringUiInfo = TodosFilteringUiInfo(),
                isLoading = false,
                hasError = false
            ),
            onIntent = {},
            onAddTodo = {},
            onTodoClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun TodosScreenEmptyPreview() {
    UrgatTheme {
        TodosScreen(
            uiState = TodosUiState(
                items = listOf(),
                filteringUiInfo = TodosFilteringUiInfo(),
                isLoading = false,
                hasError = false
            ),
            onIntent = {},
            onAddTodo = {},
            onTodoClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun TodosScreenLoadingPreview() {
    UrgatTheme {
        TodosScreen(
            uiState = TodosUiState(
                items = listOf(),
                filteringUiInfo = TodosFilteringUiInfo(),
                isLoading = true,
                hasError = false
            ),
            onIntent = {},
            onAddTodo = {},
            onTodoClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun TodosScreenErrorPreview() {
    UrgatTheme {
        TodosScreen(
            uiState = TodosUiState(
                items = listOf(),
                filteringUiInfo = TodosFilteringUiInfo(),
                isLoading = false,
                hasError = true
            ),
            onIntent = {},
            onAddTodo = {},
            onTodoClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}
