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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.theme.UrgatTheme

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
    val screenName = stringResource(R.string.overview)

    Column(
        modifier = Modifier
            .padding(dimensionResource(R.dimen.content_padding))
            .verticalScroll(rememberScrollState())
            .semantics { contentDescription = screenName }
    ) {
        TodoCard(
            onClickSeeAllTodos = onClickSeeAllTodos,
            onClickAddTodo = onClickAddTodo,
            onClickTodo = onClickTodo
        )

        Spacer(
            modifier = Modifier
                .height(dimensionResource(R.dimen.list_item_padding))
        )

        ExpensesCard(
            onClickSeeAllExpenses = onClickSeeAllExpenses,
            onClickAddExpense = onClickAddExpense,
            onClickExpense = onClickExpense
        )

        Spacer(
            modifier = Modifier
                .height(dimensionResource(R.dimen.list_item_padding))
        )

        NotesCard(
            onClickSeeAllNotes = onClickSeeAllNotes,
            onClickAddNote = onClickAddNote,
            onClickNote = onClickNote
        )

        Spacer(
            modifier = Modifier
                .height(dimensionResource(R.dimen.list_item_padding))
        )

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

    Text("Todo card")
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

@Preview
@Composable
fun OverviewScreenPreview() {
    UrgatTheme {
        OverviewScreen()
    }
}

@Preview
@Composable
fun TodoCardPreview() {
    UrgatTheme {
        TodoCard()
    }
}

@Preview
@Composable
fun ExpensesCardPreview() {
    UrgatTheme {
        ExpensesCard()
    }
}

@Preview
@Composable
fun NotesCardPreview() {
    UrgatTheme {
        NotesCard()
    }
}

@Preview
@Composable
fun WeatherCardPreview() {
    UrgatTheme {
        WeatherCard()
    }
}
