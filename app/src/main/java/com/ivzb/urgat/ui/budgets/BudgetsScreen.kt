package com.ivzb.urgat.ui.budgets

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.budget.BudgetCategory
import com.ivzb.urgat.data.expense.ExpenseCategory
import com.ivzb.urgat.data.investment.InvestmentCategory
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.domain.budget.GroupedBudgetCategories
import com.ivzb.urgat.domain.expense.GroupedExpenseCategories
import com.ivzb.urgat.domain.investment.GroupedInvestmentCategories
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.FilterMenu
import com.ivzb.urgat.ui.components.ListItem
import com.ivzb.urgat.ui.components.SortMenu
import com.ivzb.urgat.ui.components.charts.PieChart
import com.ivzb.urgat.ui.components.charts.PieChartItem
import com.ivzb.urgat.ui.components.screens.PagerScreen
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.ui.theme.budgetColor
import com.ivzb.urgat.ui.theme.expenseColor
import com.ivzb.urgat.util.annotateFormattedString
import com.ivzb.urgat.util.format
import com.ivzb.urgat.util.toLocalDate
import com.ivzb.urgat.util.toggle
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

    val expandedMonthlyIds = rememberSaveable { mutableStateSetOf<String>() }

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

                BudgetsPage.Monthly -> GroupedCategoriesPage(
                    budgetCategories = uiState.budgetCategoriesMonthly,
                    expenseCategories = uiState.expenseCategoriesMonthly,
                    investmentCategories = uiState.investmentCategoriesMonthly,
                    expandedCategoryIds = expandedMonthlyIds,
                    onCategoryClick = { categoryId -> expandedMonthlyIds.toggle(categoryId) },
                    onBudgetClick = onBudgetClick,
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
private fun GroupedCategoriesPage(
    budgetCategories: List<GroupedBudgetCategories>,
    expenseCategories: List<GroupedExpenseCategories>,
    investmentCategories: List<GroupedInvestmentCategories>,
    expandedCategoryIds: Set<String>,
    onCategoryClick: (String) -> Unit,
    onBudgetClick: (Budget) -> Unit,
    onListStateChanged: (LazyListState) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState { budgetCategories.size }
    val nestedListStates = remember(budgetCategories.size) {
        List(budgetCategories.size) { LazyListState() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        PrimaryScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 0.dp,
        ) {
            budgetCategories.forEachIndexed { index, category ->
                Tab(
                    text = {
                        Text(
                            text = category.group,
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

            val budgetCategory = budgetCategories[pageIndex]
            val expenseCategory = expenseCategories.getOrNull(pageIndex)
            val investmentCategory = investmentCategories.getOrNull(pageIndex)

            CategoriesPage(
                group = budgetCategory.group,

                budgetCategories = budgetCategory.items,
                expenseCategories = expenseCategory?.items ?: listOf(),
                investmentCategories = investmentCategory?.items ?: listOf(),

                expandedCategoryIds = expandedCategoryIds,

                onCategoryClick = onCategoryClick,
                onBudgetClick = onBudgetClick,

                listState = nestedListStates[pageIndex],
                onListStateChanged = onListStateChanged,
            )
        }
    }
}

@Composable
private fun CategoriesPage(
    group: String,

    budgetCategories: List<BudgetCategory>,
    expenseCategories: List<ExpenseCategory>,
    investmentCategories: List<InvestmentCategory>,

    expandedCategoryIds: Set<String>,

    onCategoryClick: (String) -> Unit,
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

        item {
            @Composable
            fun formattedText(name: String, value: BigDecimal): AnnotatedString {
                val valueFormatted = value.format()
                val textFormatted = stringResource(R.string.chart_text_format, name, valueFormatted).trim()

                return annotateFormattedString(
                    formattedString = textFormatted,
                    part = valueFormatted,
                    spanStyle = SpanStyle(
                        color = expenseColor(value),
                        fontWeight = FontWeight.Bold
                    ),
                )
            }

            println(budgetCategories)
            println(expenseCategories)
            println(investmentCategories)
            println()

            val data = budgetCategories.map {
                PieChartItem(
                    value = it.value.toFloat(),
                    selectedText = formattedText(it.name, it.value)
                )
            }
            val total = budgetCategories.sumOf { it.value }
            val defaultText = formattedText(group, total)

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

        budgetCategories.forEachIndexed { index, category ->
            item(key = category.id) {
                CategoryItem(
                    category = category,
                    colorIndex = index,
                    onClick = { onCategoryClick(category.id) },
                )
            }

            val isExpanded = expandedCategoryIds.contains(category.id)

            if (isExpanded) {
                // todo:
//                subBudgets(category.expenses, onBudgetClick)
            }
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

@Composable
private fun CategoryItem(
    category: BudgetCategory,
    colorIndex: Int,
    onClick: () -> Unit,
) {
    ListItem(
        textLeft = category.name,
        subTextLeft = "",

        textRight = category.value.format(),
        subTextRight = "",

        colorIndex = colorIndex,

        onClick = onClick,
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
