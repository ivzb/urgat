package com.ivzb.urgat.ui.expenses

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.ui.GroupedItems
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.EmptyStateScreen
import com.ivzb.urgat.ui.components.FilterMenu
import com.ivzb.urgat.ui.components.ItemHeaderLarge
import com.ivzb.urgat.ui.components.ItemHeaderSmall
import com.ivzb.urgat.ui.components.ListDetailsScaffold
import com.ivzb.urgat.ui.components.ListItem
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

            uiState.items.isEmpty() -> {
                EmptyStateScreen(
                    label = uiState.filteringUiInfo.emptyLabel,
                    iconRes = uiState.filteringUiInfo.emptyIconRes
                )
            }

            else -> {
                ExpensesContent(
                    groupedExpenses = uiState.items,
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
    groupedExpenses: List<GroupedItems<String, Expense>>,
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
            ItemHeaderLarge(text = stringResource(filteringUiInfo.currentFilteringLabel))
        }

        groupedExpenses.forEach { (group, expenses) ->
            item {
                ItemHeaderSmall(
                    text = group,
                    modifier = Modifier
                        .padding(vertical = dimensionResource(R.dimen.vertical_margin))
                )
            }

            items(expenses) { expense ->
                ExpenseItem(
                    expense = expense,
                    onExpenseClick = onExpenseClick
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

    ListItem(
        textLeft = date.dayOfMonth.toString(),
        subTextLeft = date.month.name.take(3),

        textRight = expense.name,
        subTextRight = (expense.amount * -1.toBigDecimal()).toPlainString(),

        subTextRightFontWeight = FontWeight.Bold,
        subTextRightColor = Color(if (expense.amount > BigDecimal.ZERO) {
            0xFFF44336
        } else {
            0xFF4CAF50
        }),

        onClick = { onExpenseClick(expense) }
    )
}

@AllDevicesPreview
@Composable
private fun ExpensesScreenPreview() {
    UrgatTheme {
        ExpensesScreen(
            uiState = ExpensesUiState(
                items = SampleData.SAMPLE_EXPENSES_GROUPED,
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