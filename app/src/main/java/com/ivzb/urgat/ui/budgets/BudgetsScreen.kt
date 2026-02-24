package com.ivzb.urgat.ui.budgets

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.budget.GroupedBudget
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.FilterMenu
import com.ivzb.urgat.ui.components.ListItem
import com.ivzb.urgat.ui.components.SortMenu
import com.ivzb.urgat.ui.components.charts.PieChart
import com.ivzb.urgat.ui.components.charts.PieChartItem
import com.ivzb.urgat.ui.components.format.formatBudgetsChart
import com.ivzb.urgat.ui.components.format.formatExpensesChart
import com.ivzb.urgat.ui.components.format.formatInvestmentsChart
import com.ivzb.urgat.ui.components.format.formatSavingsChart
import com.ivzb.urgat.ui.components.screens.PagerScreen
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.ui.theme.budgetColor
import com.ivzb.urgat.util.format
import com.ivzb.urgat.util.toLocalDate
import kotlinx.coroutines.launch
import java.math.BigDecimal

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
        searchQuery = uiState.filteringUiInfo.searchQuery,
        onSearchQueryChanged = { query -> onIntent(BudgetsIntent.SearchQueryChanged(query)) },
        onAddItem = onAddBudget,
        onBack = onBack,

        topAppBarActionsContent = {
            FilterMenu(
                items = uiState.filteringUiInfo.filterMenuItems,
                showBadge = uiState.filteringUiInfo.showFilterBadge,
                onFilter = { onIntent(BudgetsIntent.SetFiltering(it)) },
            )

            SortMenu(
                items = uiState.filteringUiInfo.sortMenuItems,
                showBadge = uiState.filteringUiInfo.showSortBadge,
                onFilter = { onIntent(BudgetsIntent.SetSorting(it)) },
            )
        },

        content = { page, listState, onListStateChanged ->
            when (page) {
                BudgetsPage.Monthly -> GroupedBudgetsPage(
                    monthlyBudgets = uiState.monthlyBudgets,
                    onListStateChanged = onListStateChanged,
                )

                BudgetsPage.Yearly -> GroupedBudgetsPage(
                    monthlyBudgets = uiState.yearlyBudgets,
                    onListStateChanged = onListStateChanged,
                )

                BudgetsPage.AllTime -> uiState.allTimeBudget?.let { budget ->
                    CategoriesPage(
                        budget = budget,
                        group = stringResource(R.string.all_time),
                        listState = listState,
                        onListStateChanged = onListStateChanged,
                    )
                }
            }
        },

        modifier = modifier,
    )
}

@Composable
fun TransactionsPage(
    budgets: List<Budget>,
    onBudgetClick: (Budget) -> Unit,

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
private fun GroupedBudgetsPage(
    monthlyBudgets: List<GroupedBudget>,
    onListStateChanged: (LazyListState) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState { monthlyBudgets.size }
    val nestedListStates = remember(monthlyBudgets.size) {
        List(monthlyBudgets.size) { LazyListState() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        PrimaryScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 0.dp,
        ) {
            monthlyBudgets.forEachIndexed { index, budget ->
                Tab(
                    text = {
                        Text(
                            text = budget.category.group,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = true,
            modifier = Modifier
                .fillMaxSize(),
        ) { pageIndex ->

            val budget = monthlyBudgets[pageIndex]

            CategoriesPage(
                budget = budget,
                listState = nestedListStates[pageIndex],
                onListStateChanged = onListStateChanged,
            )
        }
    }
}

@Composable
fun CategoriesPage(
    budget: GroupedBudget,
    group: String? = null,

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

        budgetCategoryPage(budget, group)
    }
}

fun LazyListScope.budgetCategoryPage(
    budget: GroupedBudget,
    group: String? = null,
) {

    item {
        val data = buildList {
            add(PieChartItem(
                value = budget.totalExpenses.toFloat(),
                selectedText = formatExpensesChart(stringResource(R.string.expenses), budget.totalExpenses)
            ))

            add(PieChartItem(
                value = budget.totalInvestments.toFloat(),
                selectedText = formatInvestmentsChart(stringResource(R.string.investments), budget.totalInvestmentsFormatted)
            ))

            add(PieChartItem(
                value = budget.totalSavings.toFloat(),
                selectedText = formatSavingsChart(stringResource(R.string.savings), budget.totalSavings)
            ))

            if (budget.remaining > BigDecimal.ZERO) {
                add(PieChartItem(
                    value = budget.remaining.toFloat(),
                    selectedText = formatBudgetsChart(stringResource(R.string.remaining), budget.remaining)
                ))
            }
        }

        val defaultText = formatBudgetsChart(group ?: budget.category.group, budget.total)

        PieChart(
            data = data,
            defaultText = defaultText,
            textStyle = MaterialTheme.typography.titleLarge,
        )
    }

    item {
        Spacer(
            modifier = Modifier
                .height(dimensionResource(R.dimen.vertical_margin))
        )
    }

    item {
        CategoryItem(
            name = stringResource(R.string.expenses),
            value = budget.totalExpenses.format(),
            colorIndex = 0,
        )
    }

    item {
        CategoryItem(
            name = stringResource(R.string.investments),
            value = budget.totalInvestments.format(),
            colorIndex = 1,
        )
    }

    item {
        CategoryItem(
            name = stringResource(R.string.savings),
            value = budget.totalSavings.format(),
            colorIndex = 2,
        )
    }

    item {
        CategoryItem(
            name = stringResource(R.string.remaining),
            value = budget.remaining.format(),
            colorIndex = 3,
        )
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

@Composable
private fun CategoryItem(
    name: String,
    value: String,
    colorIndex: Int,
) {
    ListItem(
        textLeft = name,
        subTextLeft = "",

        textRight = value,
        subTextRight = "",

        colorIndex = colorIndex,

        onClick = { /* NOOP */ },
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
private fun BudgetsScreenMonthlyPreview() {
    BudgetsScreenPreview(initialPage = 1)
}

@AllDevicesPreview
@Composable
private fun BudgetsScreenYearlyPreview() {
    BudgetsScreenPreview(initialPage = 2)
}

@AllDevicesPreview
@Composable
private fun BudgetsScreenAllTimePreview() {
    BudgetsScreenPreview(initialPage = 3)
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
