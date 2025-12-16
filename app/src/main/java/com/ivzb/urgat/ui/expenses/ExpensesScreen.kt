package com.ivzb.urgat.ui.expenses

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.EmptyStateScreen
import com.ivzb.urgat.ui.components.FilterMenu
import com.ivzb.urgat.ui.components.ItemHeader
import com.ivzb.urgat.ui.components.ListDetailsScaffold
import com.ivzb.urgat.ui.components.LoadingState
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.util.toLocalDate
import java.math.BigDecimal

@Composable
fun ExpensesScreen(
    viewModel: ExpensesViewModel,

    onAddExpense: () -> Unit,
    onExpenseClick: (Expense) -> Unit,
    onBack: () -> Unit,

    modifier: Modifier = Modifier,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ExpensesScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onAddExpense = onAddExpense,
        onExpenseClick = onExpenseClick,
        onBack = onBack,
        modifier = modifier
    )
}

@VisibleForTesting
@Composable
fun ExpensesScreen(
    uiState: ExpensesUiState,
    onIntent: (ExpensesIntent) -> Unit,
    onAddExpense: () -> Unit,
    onExpenseClick: (Expense) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    ListDetailsScaffold(
        selectedRoute = ExpensesRoute,
        onSearchQueryChanged = { query -> onIntent(ExpensesIntent.SearchQueryChanged(query)) },
        onAddItem = onAddExpense,
        onBack = onBack,
        topAppBarActionsContent = {
            FilterMenu(
                items = uiState.filteringUiInfo.filterMenuItems,
                onFilter = { onIntent(ExpensesIntent.SetFiltering(it)) },
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

            uiState.hasError -> {
                EmptyStateScreen(
                    label = uiState.filteringUiInfo.errorLabel,
                    iconRes = uiState.filteringUiInfo.errorIconRes
                )
            }

            else -> {
                ExpensesContent(
                    expenses = uiState.items,
                    onExpenseClick = onExpenseClick,
                    filteringUiInfo = uiState.filteringUiInfo,
                    listState = listState
                )
            }
        }
    }
}

@Composable
internal fun ExpensesContent(
    expenses: List<Expense>,
    onExpenseClick: (Expense) -> Unit,

    filteringUiInfo: ExpensesFilteringUiInfo,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = dimensionResource(R.dimen.content_padding_horizontal),
            vertical = dimensionResource(R.dimen.content_padding_vertical),
        ),
        state = listState
    ) {

        item {
            ItemHeader(title = stringResource(filteringUiInfo.currentFilteringLabel))
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

        if (expenses.isEmpty()) {
            item {
                EmptyStateScreen(
                    label = filteringUiInfo.emptyLabel,
                    iconRes = filteringUiInfo.emptyIconRes
                )
            }
        }

        item {
            Spacer(modifier = Modifier
                .height(dimensionResource(R.dimen.vertical_margin))
            )
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

@AllDevicesPreview
@Composable
private fun ExpensesScreenPreview() {
    UrgatTheme {
        ExpensesScreen(
            uiState = ExpensesUiState(
                items = SampleData.SAMPLE_EXPENSES,
                filteringUiInfo = ExpensesFilteringUiInfo(),
                isLoading = false,
                hasError = false
            ),
            onIntent = {},
            onAddExpense = {},
            onExpenseClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun ExpensesScreenEmptyPreview() {
    UrgatTheme {
        ExpensesScreen(
            uiState = ExpensesUiState(
                items = listOf(),
                filteringUiInfo = ExpensesFilteringUiInfo(),
                isLoading = false,
                hasError = false
            ),
            onIntent = {},
            onAddExpense = {},
            onExpenseClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun ExpensesScreenLoadingPreview() {
    UrgatTheme {
        ExpensesScreen(
            uiState = ExpensesUiState(
                items = listOf(),
                filteringUiInfo = ExpensesFilteringUiInfo(),
                isLoading = true,
                hasError = false
            ),
            onIntent = {},
            onAddExpense = {},
            onExpenseClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun ExpensesScreenErrorPreview() {
    UrgatTheme {
        ExpensesScreen(
            uiState = ExpensesUiState(
                items = listOf(),
                filteringUiInfo = ExpensesFilteringUiInfo(),
                isLoading = false,
                hasError = true
            ),
            onIntent = {},
            onAddExpense = {},
            onExpenseClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}