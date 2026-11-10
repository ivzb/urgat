package com.ivzb.urgat.ui.expenses

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpenseTotal.Companion.sumByCurrency
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.domain.expense.GroupedExpenses
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.FilterMenu
import com.ivzb.urgat.ui.components.ItemHeaderSmall
import com.ivzb.urgat.ui.components.ListItem
import com.ivzb.urgat.ui.components.MoreMenu
import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.components.SortMenu
import com.ivzb.urgat.ui.components.screens.PagerScreen
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.util.annotateExpenses
import com.ivzb.urgat.util.annotateExpensesTotals
import com.ivzb.urgat.util.annotateMarketPrice
import com.ivzb.urgat.util.paddingIf
import com.ivzb.urgat.util.toLocalDate
import com.ivzb.urgat.util.toggle

@Composable
fun ExpensesScreen(
    viewModel: ExpensesViewModel,

    onExpenseClick: (Expense) -> Unit,
    onBack: () -> Unit,

    modifier: Modifier = Modifier,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ExpensesScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
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

    onExpenseClick: (Expense) -> Unit,

    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    // the selection is kept by name, instead of by id, so it survives switching between tabs,
    // as every tab groups its own categories and generates new ids for them
    val expandedCategoryNames = rememberSaveable { mutableStateListOf<String>() }
    var selectedChartTitle by rememberSaveable { mutableStateOf<String?>(null) }

    PagerScreen(
        uiState = uiState,

        route = ExpensesRoute,
        searchQuery = uiState.filteringUiInfo.searchQuery,
        onSearchQueryChanged = { query -> onIntent(ExpensesIntent.SearchQueryChanged(query)) },
        onBack = onBack,

        topAppBarActionsContent = {
            ExpensesTopAppBarActions(
                uiState = uiState,
                onIntent = onIntent,
            )
        },

        content = { page, listState, onListStateChanged ->
            ExpensesPageContent(
                page = page,
                uiState = uiState,
                onIntent = onIntent,
                expandedCategoryNames = expandedCategoryNames,
                onExpenseClick = onExpenseClick,
                selectedChartTitle = selectedChartTitle,
                onSelectedChartTitleChanged = { title -> selectedChartTitle = title },
                listState = listState,
                onListStateChanged = onListStateChanged,
            )
        },

        modifier = modifier,
    )
}

@Composable
private fun ExpensesTopAppBarActions(
    uiState: ExpensesUiState,
    onIntent: (ExpensesIntent) -> Unit,
) {
    FilterMenu(
        items = uiState.filteringUiInfo.filterMenuItems,
        showBadge = uiState.filteringUiInfo.showFilterBadge,
        onFilter = { onIntent(ExpensesIntent.SetFiltering(it)) },
    )

    SortMenu(
        items = uiState.filteringUiInfo.sortMenuItems,
        showBadge = uiState.filteringUiInfo.showSortBadge,
        onFilter = { onIntent(ExpensesIntent.SetSorting(it)) },
    )

    MoreMenu(
        listOfNotNull(
            (R.string.reset_filters to { onIntent(ExpensesIntent.ResetFilters) }).takeIf { uiState.filteringUiInfo.hasAppliedFilters },
        )
    )
}

@Composable
private fun ExpensesPageContent(
    page: Page,
    uiState: ExpensesUiState,
    onIntent: (ExpensesIntent) -> Unit,
    expandedCategoryNames: MutableList<String>,
    onExpenseClick: (Expense) -> Unit,
    selectedChartTitle: String?,
    onSelectedChartTitleChanged: (String?) -> Unit,
    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {
    when (page) {
        ExpensesPage.Monthly -> GroupedCategoriesPage(
            categories = uiState.categoriesMonthly,
            averages = uiState.averagesMonthly,
            filteringUiInfo = uiState.filteringUiInfo,
            expandedCategoryNames = expandedCategoryNames,
            onCategoryClick = { categoryName -> expandedCategoryNames.toggle(categoryName) },
            onExpenseClick = onExpenseClick,
            onResetFilters = { onIntent(ExpensesIntent.ResetFilters) },
            selectedChartTitle = selectedChartTitle,
            onSelectedChartTitleChanged = onSelectedChartTitleChanged,
            onListStateChanged = onListStateChanged,
        )

        ExpensesPage.Yearly -> GroupedCategoriesPage(
            categories = uiState.categoriesYearly,
            averages = uiState.averagesYearly,
            filteringUiInfo = uiState.filteringUiInfo,
            expandedCategoryNames = expandedCategoryNames,
            onCategoryClick = { categoryName -> expandedCategoryNames.toggle(categoryName) },
            onExpenseClick = onExpenseClick,
            onResetFilters = { onIntent(ExpensesIntent.ResetFilters) },
            selectedChartTitle = selectedChartTitle,
            onSelectedChartTitleChanged = onSelectedChartTitleChanged,
            onListStateChanged = onListStateChanged,
        )

        ExpensesPage.AllTime -> CategoriesPage(
            group = stringResource(R.string.all_time),
            total = annotateExpensesTotals(
                uiState.categoriesAllTime
                    .flatMap { it.totals }
                    .sumByCurrency()
            ),
            categories = uiState.categoriesAllTime,
            expandedCategoryNames = expandedCategoryNames,
            average = uiState.averageAllTime,
            listState = listState,
            onCategoryClick = { categoryName -> expandedCategoryNames.toggle(categoryName) },
            onExpenseClick = onExpenseClick,
            filteringUiInfo = uiState.filteringUiInfo,
            onResetFilters = { onIntent(ExpensesIntent.ResetFilters) },
            selectedChartTitle = selectedChartTitle,
            onSelectedChartTitleChanged = onSelectedChartTitleChanged,
            onListStateChanged = onListStateChanged,
        )
    }
}

@Composable
fun TransactionsPage(
    expenses: List<GroupedExpenses>,
    onExpenseClick: (Expense) -> Unit,

    header: (@Composable () -> Unit)? = null,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    LaunchedEffect(Unit) {
        onListStateChanged(listState)
    }

    LazyColumn(
        contentPadding = PaddingValues(
            horizontal = dimensionResource(R.dimen.content_padding_horizontal),
            vertical = dimensionResource(R.dimen.content_padding_vertical),
        ),
        state = listState,
        modifier = Modifier
            .fillMaxSize(),
    ) {

        item {
            header?.invoke()
        }

        expenses.forEachIndexed { index, (group, total, expenses) ->
            item {
                ItemHeaderSmall(
                    text = group,
                    total = annotateExpensesTotals(total),
                    modifier = Modifier
                        .paddingIf(index == 0, bottom = dimensionResource(R.dimen.vertical_margin))
                        .paddingIf(index > 0, vertical = dimensionResource(R.dimen.vertical_margin))
                        .padding(horizontal = dimensionResource(R.dimen.list_item_padding))
                )
            }

            items(
                items = expenses,
                key = { expense -> expense.id },
            ) { expense ->

                ExpenseItem(
                    expense = expense,
                    onExpenseClick = onExpenseClick
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

    ListItem(
        textLeft = date.dayOfMonth.toString(),
        subTextLeft = date.month.name.take(3),

        textRight = expense.name,
        subTextRight = annotateExpenses(expense.amount, expense.currency),

        onClick = { onExpenseClick(expense) }
    )
}

@AllDevicesPreview
@Composable
private fun ExpensesScreenPreview(initialPage: Int = 0) {
    UrgatTheme {
        ExpensesScreen(
            uiState = ExpensesUiState(
                expenses = SampleData.SAMPLE_EXPENSES_GROUPED,
                categoriesMonthly = SampleData.SAMPLE_EXPENSES_CATEGORIES_MONTHLY,
                categoriesYearly = SampleData.SAMPLE_EXPENSES_CATEGORIES_YEARLY,
                categoriesAllTime = SampleData.SAMPLE_EXPENSES_CATEGORIES_ALL_TIME,
                averagesMonthly = SampleData.SAMPLE_EXPENSES_AVERAGES_MONTHLY,
                averagesYearly = SampleData.SAMPLE_EXPENSES_AVERAGES_YEARLY,
                averageAllTime = SampleData.SAMPLE_EXPENSES_AVERAGE_ALL_TIME,
                filteringUiInfo = ExpensesFilteringUiInfo(),
                isLoading = false,
                hasError = false,
                initialPage = initialPage,
            ),
            onIntent = {},
            onExpenseClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun ExpensesScreenMonthlyPreview() {
    ExpensesScreenPreview(initialPage = 1)
}

@AllDevicesPreview
@Composable
private fun ExpensesScreenYearlyPreview() {
    ExpensesScreenPreview(initialPage = 2)
}

@AllDevicesPreview
@Composable
private fun ExpensesScreenAllTimePreview() {
    ExpensesScreenPreview(initialPage = 3)
}

@AllDevicesPreview
@Composable
private fun ExpensesScreenEmptyPreview() {
    UrgatTheme {
        ExpensesScreen(
            uiState = ExpensesUiState(
                expenses = listOf(),
                categoriesMonthly = listOf(),
                filteringUiInfo = ExpensesFilteringUiInfo(),
                isLoading = false,
                hasError = false
            ),
            onIntent = {},
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
                expenses = listOf(),
                categoriesMonthly = listOf(),
                filteringUiInfo = ExpensesFilteringUiInfo(),
                isLoading = true,
                hasError = false
            ),
            onIntent = {},
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
                expenses = listOf(),
                categoriesMonthly = listOf(),
                filteringUiInfo = ExpensesFilteringUiInfo(),
                isLoading = false,
                hasError = true
            ),
            onIntent = {},
            onExpenseClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}
