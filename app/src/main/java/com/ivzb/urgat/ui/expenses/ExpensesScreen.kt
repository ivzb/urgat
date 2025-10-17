package com.ivzb.urgat.ui.expenses

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ivzb.urgat.R
import com.ivzb.urgat.navigation.ExpensesDestination
import com.ivzb.urgat.ui.components.LandscapePreview
import com.ivzb.urgat.ui.components.MenuScaffold
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun ExpensesScreen(
    onAddExpense: () -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: ExpensesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ExpensesScreenScaffold(
        uiState = uiState,
        onSearchQueryChanged = {},
        onAddExpense = onAddExpense,
        navController = navController,
        modifier = modifier
    )
}

@Composable
private fun ExpensesScreenScaffold(
    uiState: ExpensesUiState,
    onSearchQueryChanged: (String) -> Unit,
    onAddExpense: () -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    MenuScaffold(
        selectedDestination = ExpensesDestination,
        onSearchQueryChanged = onSearchQueryChanged,
        onAddItem = onAddExpense,
        withTopAppBarActions = (uiState as? ExpensesUiState.Success)?.data?.isNotEmpty() ?: false,
        navController = navController,
        modifier = modifier
    ) { listState ->

        when (uiState) {
            ExpensesUiState.Loading -> {
                Text("Loading")
            }

            is ExpensesUiState.Error -> {
                Text("Error")
            }

            is ExpensesUiState.Success -> {
                ExpensesScreenContent(
                    expenses = uiState.data,
                    listState = listState,
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
internal fun ExpensesScreenContent(
    expenses: List<String>,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {

    Box(modifier = modifier) {
        LazyColumn(
            contentPadding = PaddingValues(
                horizontal = dimensionResource(R.dimen.horizontal_margin),
                vertical = dimensionResource(R.dimen.vertical_margin)
            ),
            state = listState
        ) {
            item { Header(title = stringResource(R.string.expenses)) }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            items(expenses) { expense ->
                ExpenseRow(expense)
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun Header(
    title: String
) {
    Text(
        text = title,
        modifier = Modifier.semantics { heading() },
        style = MaterialTheme.typography.headlineLarge
    )
}

@Composable
private fun ExpenseRow(expense: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = expense,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview
@LandscapePreview
@Composable
private fun ExpensesScreenScaffoldPreview() {
    UrgatTheme {
        ExpensesScreenScaffold(
            uiState = ExpensesUiState.Success(
                data = listOf("Compose", "Room", "Kotlin"),
            ),
            onSearchQueryChanged = {},
            onAddExpense = {},
            navController = rememberNavController(),
            modifier = Modifier
        )
    }
}

@Preview
@LandscapePreview
@Composable
private fun ExpensesScreenScaffoldLoadingPreview() {
    UrgatTheme {
        ExpensesScreenScaffold(
            uiState = ExpensesUiState.Loading,
            onSearchQueryChanged = {},
            onAddExpense = {},
            navController = rememberNavController(),
            modifier = Modifier
        )
    }
}

@Preview
@LandscapePreview
@Composable
private fun ExpensesScreenScaffoldErrorPreview() {
    UrgatTheme {
        ExpensesScreenScaffold(
            uiState = ExpensesUiState.Error(
                throwable = IllegalArgumentException()
            ),
            onSearchQueryChanged = {},
            onAddExpense = {},
            navController = rememberNavController(),
            modifier = Modifier
        )
    }
}

@Preview
@Composable
private fun ExpensesScreenContentPreview() {
    UrgatTheme {
        ExpensesScreenContent(
            expenses = listOf("Compose", "Room", "Kotlin"),
            listState = rememberLazyListState()
        )
    }
}
