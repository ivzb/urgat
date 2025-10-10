package com.ivzb.urgat.ui.overview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.ivzb.urgat.ui.todo.TodosContent

@Composable
fun OverviewScreen(
    onSeeAllTodos: () -> Unit,
    onAddTodo: () -> Unit,
    onTodoClick: (Todo) -> Unit,
    onSeeAllExpenses: () -> Unit,
    onAddExpense: () -> Unit,
    onExpenseClick: (String) -> Unit,
    onSeeAllNotes: () -> Unit,
    onAddNote: () -> Unit,
    onNoteClick: (String) -> Unit,
    onSeeAllWeather: () -> Unit,
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
                todos = uiState.todoItems,
                todoFilteringUiInfo = uiState.todoFilteringUiInfo,
                onTodoClick = onTodoClick,
                onTodoCheckedChange = viewModel::completeTodo,
                onSeeAllTodos = onSeeAllTodos,
                onAddTodo = onAddTodo,
                onExpenseClick = onExpenseClick,
                onSeeAllNotes = onSeeAllNotes,
                onAddNote = onAddNote,
                onNoteClick = onNoteClick,
                onAddExpense = onAddExpense,
                onSeeAllExpenses = onSeeAllExpenses,
                onSeeAllWeather = onSeeAllWeather,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun OverviewContent(
    todos: List<Todo>,
    todoFilteringUiInfo: TodoFilteringUiInfo,
    onTodoClick: (Todo) -> Unit,
    onTodoCheckedChange: (Todo, Boolean) -> Unit,
    onSeeAllTodos: () -> Unit,
    onAddTodo: () -> Unit,
    onSeeAllExpenses: () -> Unit,
    onAddExpense: () -> Unit,
    onExpenseClick: (String) -> Unit,
    onSeeAllNotes: () -> Unit,
    onAddNote: () -> Unit,
    onNoteClick: (String) -> Unit,
    onSeeAllWeather: () -> Unit,
    modifier: Modifier
) {
    val screenName = stringResource(R.string.overview)

    Column(
        modifier = modifier
            .padding(dimensionResource(R.dimen.content_padding))
            .semantics { contentDescription = screenName }
    ) {
        TodoCard(
            todos = todos,
            todoFilteringUiInfo = todoFilteringUiInfo,
            onTodoClick = onTodoClick,
            onTodoCheckedChange = onTodoCheckedChange,
            onSeeAllTodos = onSeeAllTodos,
            onAddTodo = onAddTodo,
        )

        Spacer(
            modifier = Modifier
                .height(dimensionResource(R.dimen.list_item_padding))
        )

        ExpensesCard(
            onSeeAllExpenses = onSeeAllExpenses,
            onAddExpense = onAddExpense,
            onExpenseClick = onExpenseClick
        )

        Spacer(
            modifier = Modifier
                .height(dimensionResource(R.dimen.list_item_padding))
        )

        NotesCard(
            onSeeAllNotes = onSeeAllNotes,
            onAddNote = onAddNote,
            onNoteClick = onNoteClick
        )

        Spacer(
            modifier = Modifier
                .height(dimensionResource(R.dimen.list_item_padding))
        )

        WeatherCard(
            onSeeAllWeather = onSeeAllWeather
        )
    }

}

@Composable
private fun TodoCard(
    todos: List<Todo>,
    todoFilteringUiInfo: TodoFilteringUiInfo,
    onTodoClick: (Todo) -> Unit,
    onTodoCheckedChange: (Todo, Boolean) -> Unit,
    onSeeAllTodos: () -> Unit,
    onAddTodo: () -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        todos.isEmpty() -> {
            EmptyState(
                label = todoFilteringUiInfo.emptyLabel,
                iconRes = todoFilteringUiInfo.emptyIconRes,
                modifier = modifier
            )
        }

        else -> {
            TodosContent(
                todos = todos,
                currentFilteringLabel = todoFilteringUiInfo.currentFilteringLabel,
                onTodoClick = onTodoClick,
                onTodoCheckedChange = onTodoCheckedChange,
                modifier = modifier
            )

//        Row {
//            Text("see all todos button")
//            Text("|")
//            Text("add todo button")
//        }
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
fun OverviewScreenPreview() {
    UrgatTheme {
        OverviewScreen(
            onSeeAllTodos = {},
            onAddTodo = {},
            onTodoClick = {},
            onSeeAllExpenses = {},
            onAddExpense = {},
            onExpenseClick = {},
            onSeeAllNotes = {},
            onAddNote = {},
            onNoteClick = {},
            onSeeAllWeather = {}
        )
    }
}

@Preview
@Composable
fun TodoCardPreview() {
    UrgatTheme {
        TodoCard(
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
            onSeeAllTodos = {},
            onAddTodo = {},
        )
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
