package com.ivzb.urgat.ui.overview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun OverviewScreen(
    onClickSeeAllTodos: () -> Unit = {},
    onClickAddTodo: () -> Unit = {},
    onClickTodo: (String) -> Unit = {},
    onClickSeeAllExpenses: () -> Unit = {},
    onClickAddExpense: () -> Unit = {},
    onClickExpense: (String) -> Unit = {},
    onClickSeeAllNotes: () -> Unit = {},
    onClickAddNote: () -> Unit = {},
    onClickNote: (String) -> Unit = {},
    onClickSeeAllWeather: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .semantics { contentDescription = "Overivew Screen" }
    ) {
        TodoCard(
            onClickSeeAllTodos = onClickSeeAllTodos,
            onClickAddTodo = onClickAddTodo,
            onClickTodo = onClickTodo
        )

        Spacer(Modifier.height(UrgatDefaultPadding))

        ExpensesCard(
            onClickSeeAllExpenses = onClickSeeAllExpenses,
            onClickAddExpense = onClickAddExpense,
            onClickExpense = onClickExpense
        )

        Spacer(Modifier.height(UrgatDefaultPadding))

        NotesCard(
            onClickSeeAllNotes = onClickSeeAllNotes,
            onClickAddNote = onClickAddNote,
            onClickNote = onClickNote
        )

        Spacer(Modifier.height(UrgatDefaultPadding))

        WeatherCard(
            onClickSeeAllWeather = onClickSeeAllWeather
        )
    }
}

@Composable
private fun TodoCard(
    onClickSeeAllTodos: () -> Unit = {},
    onClickAddTodo: () -> Unit = {},
    onClickTodo: (String) -> Unit = {},
) {

    Text("Todos card")
}

@Composable
private fun ExpensesCard(
    onClickSeeAllExpenses: () -> Unit = {},
    onClickAddExpense: () -> Unit = {},
    onClickExpense: (String) -> Unit = {},
) {

    Text("Expenses card")
}

@Composable
private fun NotesCard(
    onClickSeeAllNotes: () -> Unit = {},
    onClickAddNote: () -> Unit = {},
    onClickNote: (String) -> Unit = {},
) {

    Text("Notes card")
}

@Composable
private fun WeatherCard(
    onClickSeeAllWeather: () -> Unit = {}
) {

    Text("Weather card")
}

private val UrgatDefaultPadding = 12.dp
