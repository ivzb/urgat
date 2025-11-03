package com.ivzb.urgat.ui.expenses

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.navigation.ExpensesDestination
import com.ivzb.urgat.ui.components.EmptyStateScreen
import com.ivzb.urgat.ui.components.FilterExpensesMenu
import com.ivzb.urgat.ui.components.ItemHeader
import com.ivzb.urgat.ui.components.LandscapePreview
import com.ivzb.urgat.ui.components.LoadingState
import com.ivzb.urgat.ui.components.UrgatScaffold
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.util.toLocalDate
import java.math.BigDecimal

@Composable
fun ExpensesScreen(
    onAddExpense: () -> Unit,
    onExpenseClick: (Expense) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExpensesViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ExpensesScreenScaffold(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onAddExpense = onAddExpense,
        onExpenseClick = onExpenseClick,
        modifier = modifier
    )
}

@Composable
private fun ExpensesScreenScaffold(
    uiState: ExpensesUiState,
    onIntent: (ExpensesIntent) -> Unit,
    onAddExpense: () -> Unit,
    onExpenseClick: (Expense) -> Unit,
    modifier: Modifier = Modifier
) {

    UrgatScaffold(
        selectedDestination = ExpensesDestination,
        onSearchQueryChanged = { query -> onIntent(ExpensesIntent.SearchQueryChanged(query)) },
        onAddItem = onAddExpense,
        withTopAppBarActions = uiState.items.isNotEmpty(),
        topAppBarActionsContent = {
            FilterExpensesMenu(
                onFilterAllExpenses = { onIntent(ExpensesIntent.SetFiltering(ExpensesFilterType.ALL_EXPENSES)) },
                onFilterDebitExpenses = { onIntent(ExpensesIntent.SetFiltering(ExpensesFilterType.DEBIT_EXPENSES)) },
                onFilterCostExpenses = { onIntent(ExpensesIntent.SetFiltering(ExpensesFilterType.COST_EXPENSES)) },
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
                ExpensesContent(
                    expenses = uiState.items,
                    currentFilteringLabel = uiState.filteringUiInfo.currentFilteringLabel,
                    onExpenseClick = onExpenseClick,
                    listState = listState
                )
            }
        }
    }
}

@Composable
internal fun ExpensesContent(
    expenses: List<Expense>,
    @StringRes currentFilteringLabel: Int,
    onExpenseClick: (Expense) -> Unit,
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
            item {
                ItemHeader(title = stringResource(currentFilteringLabel))
            }

            item {
                Spacer(modifier = Modifier
                    .height(dimensionResource(R.dimen.vertical_margin))
                )
            }

            items(expenses) { expense ->
                ExpenseItem(
                    expense = expense,
                    onExpenseClick = onExpenseClick
                )
            }

            item {
                Spacer(modifier = Modifier
                    .height(dimensionResource(R.dimen.vertical_margin))
                )
            }
        }
    }
}

@Composable
fun ExpenseItem(
    expense: Expense,
    onExpenseClick: (Expense) -> Unit
) {
    val date = remember { expense.date.toLocalDate() }

    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onExpenseClick(expense) }
                .padding(dimensionResource(R.dimen.list_item_padding)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = date.dayOfMonth.toString(),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall,
                )

                Text(
                    text = date.month.name.take(3),
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column (
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = expense.name,
                    style = MaterialTheme.typography.bodySmall,
                )

                Text(
                    text = (expense.amount * -1.toBigDecimal()).toPlainString(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(if (expense.amount > BigDecimal.ZERO) {
                        0xFFF44336
                    } else {
                        0xFF4CAF50
                    })
                )
            }
        }
    }
}

@Preview
@LandscapePreview
@Composable
private fun ExpensesScreenScaffoldPreview() {
    UrgatTheme {
        ExpensesScreenScaffold(
            uiState = ExpensesUiState(
                items = listOf(
                    Expense(
                        id = "ID 1",
                        name = "Food",
                        amount = 10.toBigDecimal(),
                        category = "food",
                        date = 1761335214,
                        isDeleted = false,
                        createdAt = 0,
                    ),
                    Expense(
                        id = "ID 2",
                        name = "Stuff",
                        amount = 23.23.toBigDecimal(),
                        category = "transport",
                        date = 1761335214,
                        isDeleted = false,
                        createdAt = 0,
                    ),
                    Expense(
                        id = "ID 3",
                        name = "Salary",
                        amount = -920.toBigDecimal(),
                        category = "income",
                        date = 1761335214,
                        isDeleted = false,
                        createdAt = 0,
                    )
                ),
                filteringUiInfo = ExpensesFilteringUiInfo(),
                isLoading = false,
                hasError = false
            ),
            onIntent = {},
            onAddExpense = {},
            onExpenseClick = {},
            modifier = Modifier
        )
    }
}

@Preview
@LandscapePreview
@Composable
private fun ExpensesScreenScaffoldEmptyPreview() {
    UrgatTheme {
        ExpensesScreenScaffold(
            uiState = ExpensesUiState(
                items = listOf(),
                filteringUiInfo = ExpensesFilteringUiInfo(),
                isLoading = false,
                hasError = false
            ),
            onIntent = {},
            onAddExpense = {},
            onExpenseClick = {},
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
            uiState = ExpensesUiState(
                items = listOf(),
                filteringUiInfo = ExpensesFilteringUiInfo(),
                isLoading = true,
                hasError = false
            ),
            onIntent = {},
            onAddExpense = {},
            onExpenseClick = {},
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
            uiState = ExpensesUiState(
                items = listOf(),
                filteringUiInfo = ExpensesFilteringUiInfo(),
                isLoading = false,
                hasError = true
            ),
            onIntent = {},
            onAddExpense = {},
            onExpenseClick = {},
            modifier = Modifier
        )
    }
}

@Preview
@LandscapePreview
@Composable
private fun ExpensesScreenContentPreview() {
    UrgatTheme {
        ExpensesContent(
            expenses = listOf(
                Expense(
                    id = "ID 1",
                    name = "Food",
                    amount = 10.toBigDecimal(),
                    category = "food",
                    date = 1761335214,
                    isDeleted = false,
                    createdAt = 0,
                ),
                Expense(
                    id = "ID 1",
                    name = "Stuff",
                    amount = 23.23.toBigDecimal(),
                    category = "transport",
                    date = 1761335214,
                    isDeleted = false,
                    createdAt = 0,
                )
            ),
            currentFilteringLabel = R.string.all_expenses,
            onExpenseClick = {},
            listState = rememberLazyListState()
        )
    }
}

@Preview
@LandscapePreview
@Composable
private fun ExpenseItemPreview() {
    UrgatTheme {
        ExpenseItem(
            expense = Expense(
                id = "ID 1",
                name = "Food",
                amount = 10.toBigDecimal(),
                category = "food",
                date = 1761335214,
                isDeleted = false,
                createdAt = 0,
            ),
            onExpenseClick = {}
        )
    }
}
