package com.ivzb.urgat.ui.budgets

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.FilterMenu
import com.ivzb.urgat.ui.components.ListItem
import com.ivzb.urgat.ui.components.SortMenu
import com.ivzb.urgat.ui.components.screens.PagerScreen
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.ui.theme.budgetColor
import com.ivzb.urgat.ui.theme.expenseColor
import com.ivzb.urgat.util.format
import com.ivzb.urgat.util.toLocalDate

@Composable
fun BudgetsScreen(
    viewModel: BudgetsViewModel,

    onAddBudget: () -> Unit,
    onBudgetClick: (Budget) -> Unit,
    onBack: () -> Unit,

    modifier: Modifier = Modifier,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BudgetsScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onAddBudget = onAddBudget,
        onBudgetClick = onBudgetClick,
        onBack = onBack,
        modifier = modifier
    )
}

@VisibleForTesting
@Composable
fun BudgetsScreen(
    uiState: BudgetsUiState,
    onIntent: (BudgetsIntent) -> Unit,

    onAddBudget: () -> Unit,
    onBudgetClick: (Budget) -> Unit,
    onBack: () -> Unit,

    modifier: Modifier = Modifier,
) {

    PagerScreen(
        uiState = uiState,

        route = BudgetsRoute,
        onSearchQueryChanged = { query -> onIntent(BudgetsIntent.SearchQueryChanged(query)) },
        onAddItem = onAddBudget,
        onBack = onBack,

        topAppBarActionsContent = {
            FilterMenu(
                items = uiState.filteringUiInfo.filterMenuItems,
                onFilter = { onIntent(BudgetsIntent.SetFiltering(it)) },
            )

            SortMenu(
                items = uiState.filteringUiInfo.sortMenuItems,
                onFilter = { onIntent(BudgetsIntent.SetSorting(it)) },
            )
        },

        content = { page, listState, onListStateChanged ->
            when (page) {
                BudgetsPage.Transactions -> TransactionsPage(
                    budgets = uiState.budgets,
                    onBudgetClick = onBudgetClick,

                    listState = listState,
                    onListStateChanged = onListStateChanged,
                )
            }
        },

        modifier = modifier,
    )
}

@Composable
private fun TransactionsPage(
    budgets: List<Budget>,
    onBudgetClick: (Budget) -> Unit,
    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    LaunchedEffect(Unit) {
        onListStateChanged(listState)
    }

    LazyColumn(
        contentPadding = PaddingValues(
            horizontal = dimensionResource(R.dimen.horizontal_margin),
            vertical = dimensionResource(R.dimen.vertical_margin),
        ),
        state = listState,
        modifier = Modifier
            .fillMaxSize(),
    ) {

        items(
            items = budgets,
            key = { budget -> budget.date },
        ) { budget ->

            BudgetItem(
                budget = budget,
                onBudgetClick = onBudgetClick
            )
        }
    }
}

@Composable
fun BudgetItem(
    budget: Budget,
    onBudgetClick: (Budget) -> Unit
) {

    val date = remember { budget.date.toLocalDate() }

    ListItem(
        textLeft = "${date.dayOfMonth} ${date.month.name.take(3)}",
        subTextLeft = date.year.toString(),

        textRight = budget.income.format(),
        subTextRight = budget.total?.format() ?: "",

        subTextRightFontWeight = FontWeight.Bold,
        subTextRightColor = budgetColor(budget.total),

        onClick = { onBudgetClick(budget) }
    )
}

@AllDevicesPreview
@Composable
private fun BudgetsScreenPreview(initialPage: Int = 0) {
    UrgatTheme {
        BudgetsScreen(
            uiState = BudgetsUiState(
                budgets = SampleData.SAMPLE_BUDGETS,
                filteringUiInfo = BudgetsFilteringUiInfo(),
                isLoading = false,
                hasError = false,
                initialPage = initialPage,
            ),
            onIntent = {},
            onAddBudget = {},
            onBudgetClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun BudgetsScreenEmptyPreview() {
    UrgatTheme {
        BudgetsScreen(
            uiState = BudgetsUiState(
                budgets = listOf(),
                filteringUiInfo = BudgetsFilteringUiInfo(),
                isLoading = false,
                hasError = false,
            ),
            onIntent = {},
            onAddBudget = {},
            onBudgetClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun BudgetsScreenLoadingPreview() {
    UrgatTheme {
        BudgetsScreen(
            uiState = BudgetsUiState(
                isLoading = true,
            ),
            onIntent = {},
            onAddBudget = {},
            onBudgetClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun BudgetsScreenErrorPreview() {
    UrgatTheme {
        BudgetsScreen(
            uiState = BudgetsUiState(
                hasError = true,
            ),
            onIntent = {},
            onAddBudget = {},
            onBudgetClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}
