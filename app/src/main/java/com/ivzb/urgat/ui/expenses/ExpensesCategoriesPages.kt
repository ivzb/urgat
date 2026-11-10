package com.ivzb.urgat.ui.expenses

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ivzb.urgat.R
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpenseAverage
import com.ivzb.urgat.data.expense.ExpenseCategory
import com.ivzb.urgat.domain.expense.GroupedExpenseCategories
import com.ivzb.urgat.ui.components.EmptyState
import com.ivzb.urgat.ui.components.FilteringUiInfo
import com.ivzb.urgat.ui.components.ListItem
import com.ivzb.urgat.ui.components.charts.PieChart
import com.ivzb.urgat.ui.components.charts.PieChartItem
import com.ivzb.urgat.util.annotateChart
import com.ivzb.urgat.util.annotateExpensesTotals
import com.ivzb.urgat.util.isFirst
import com.ivzb.urgat.util.isLast
import com.ivzb.urgat.util.paddingIf
import com.ivzb.urgat.util.rememberLazyListStates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun GroupedCategoriesPage(
    categories: List<GroupedExpenseCategories>,
    averages: Map<String, ExpenseAverage>,
    filteringUiInfo: FilteringUiInfo,
    expandedCategoryNames: List<String>,
    onCategoryClick: (String) -> Unit,
    onExpenseClick: (Expense) -> Unit,
    onResetFilters: () -> Unit,
    selectedChartTitle: String?,
    onSelectedChartTitleChanged: (String?) -> Unit,
    onListStateChanged: (LazyListState) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState { categories.size }
    val nestedListStates = rememberLazyListStates(categories.size)

    if (categories.isEmpty()) {
        EmptyState(
            filteringUiInfo = filteringUiInfo,
            onButtonClick = { onResetFilters() },
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        CategoriesTabRow(
            categories = categories,
            pagerState = pagerState,
            scope = scope,
        )

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = true,
            modifier = Modifier
                .fillMaxSize(),
        ) { pageIndex ->

            val category = categories[pageIndex]

            CategoriesPage(
                group = category.group,
                total = annotateExpensesTotals(category.total),

                categories = category.items,
                expandedCategoryNames = expandedCategoryNames,

                average = averages[category.group] ?: ExpenseAverage.EMPTY,

                filteringUiInfo = filteringUiInfo,
                onResetFilters = onResetFilters,

                onCategoryClick = onCategoryClick,
                onExpenseClick = onExpenseClick,

                selectedChartTitle = selectedChartTitle,
                onSelectedChartTitleChanged = onSelectedChartTitleChanged,

                listState = nestedListStates[pageIndex],
                onListStateChanged = onListStateChanged,
            )
        }
    }
}

@Composable
private fun CategoriesTabRow(
    categories: List<GroupedExpenseCategories>,
    pagerState: PagerState,
    scope: CoroutineScope,
) {
    PrimaryScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        edgePadding = 0.dp,
    ) {
        categories.forEachIndexed { index, category ->
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
}

@Composable
internal fun CategoriesPage(
    group: String,
    total: AnnotatedString?,

    categories: List<ExpenseCategory>,
    expandedCategoryNames: List<String>,

    average: ExpenseAverage,

    filteringUiInfo: FilteringUiInfo,
    onResetFilters: () -> Unit,

    onCategoryClick: (String) -> Unit,
    onExpenseClick: (Expense) -> Unit,

    selectedChartTitle: String?,
    onSelectedChartTitleChanged: (String?) -> Unit,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    LaunchedEffect(Unit) {
        onListStateChanged(listState)
    }

    if (categories.isEmpty()) {
        EmptyState(
            filteringUiInfo = filteringUiInfo,
            onButtonClick = { onResetFilters() },
        )
        return
    }

    val averagePrefix = stringResource(R.string.average_monthly)

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
            CategoriesChart(
                group = group,
                total = total,
                categories = categories,
                expandedCategoryNames = expandedCategoryNames,
                average = average,
                averagePrefix = averagePrefix,
                selectedChartTitle = selectedChartTitle,
                onSelectedChartTitleChanged = onSelectedChartTitleChanged,
            )
        }

        item {
            Spacer(
                modifier = Modifier
                    .height(dimensionResource(R.dimen.vertical_margin))
            )
        }

        categoryItems(
            categories = categories,
            expandedCategoryNames = expandedCategoryNames,
            onCategoryClick = onCategoryClick,
            onExpenseClick = onExpenseClick,
        )
    }
}

@Composable
private fun CategoriesChart(
    group: String,
    total: AnnotatedString?,
    categories: List<ExpenseCategory>,
    expandedCategoryNames: List<String>,
    average: ExpenseAverage,
    averagePrefix: String,
    selectedChartTitle: String?,
    onSelectedChartTitleChanged: (String?) -> Unit,
) {
    val lastExpandedName = expandedCategoryNames.lastOrNull()
    val expandedCategory = categories.firstOrNull { it.name == lastExpandedName }

    val averageTotals = expandedCategory
        ?.let { average.categories[it.name].orEmpty() }
        ?: average.totals

    val averageText = annotateExpensesTotals(averageTotals, prefix = "$averagePrefix ")

    val data: List<PieChartItem>
    val defaultText: AnnotatedString

    if (expandedCategory == null) {
        // all categories pie chart
        data = categories.map {
            PieChartItem(
                value = it.expenses.sumOf { it.amount }.toFloat(),
                title = it.name,
                total = annotateExpensesTotals(it.totals),
            )
        }
        defaultText = annotateChart(group, total, averageText)
    } else {
        // expanded category pie chart
        data = expandedCategory.expenses
            .groupBy { it.name }
            .map { (name, expenses) ->
                val expenseTotal = Expense.total(expenses)

                PieChartItem(
                    value = expenses.sumOf { it.amount }.toFloat(),
                    title = name,
                    total = annotateExpensesTotals(expenseTotal),
                )
            }
        defaultText = annotateChart(expandedCategory.name, annotateExpensesTotals(expandedCategory.totals), averageText)
    }

    PieChart(
        data = data,
        defaultText = defaultText,
        textStyle = MaterialTheme.typography.titleLarge,
        selectedTitle = selectedChartTitle,
        onSelectedTitleChanged = onSelectedChartTitleChanged,
    )
}

private fun LazyListScope.categoryItems(
    categories: List<ExpenseCategory>,
    expandedCategoryNames: List<String>,
    onCategoryClick: (String) -> Unit,
    onExpenseClick: (Expense) -> Unit,
) {
    categories.forEachIndexed { index, category ->
        item(key = category.id) {
            CategoryItem(
                category = category,
                colorIndex = index,
                onClick = { onCategoryClick(category.name) },
            )
        }

        val isExpanded = expandedCategoryNames.contains(category.name)

        if (isExpanded) {
            subExpenses(category.expenses, onExpenseClick)
        }
    }
}

private fun LazyListScope.subExpenses(
    expenses: List<Expense>,
    onExpenseClick: (Expense) -> Unit,
) {
    itemsIndexed(
        expenses,
        key = { _, expense -> expense.id },
    ) { index, expense ->

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.horizontal_margin))
                .paddingIf(
                    condition = index.isFirst(),
                    top = dimensionResource(R.dimen.expanded_group_vertical_margin)
                )
                .paddingIf(
                    condition = index.isLast(expenses.size),
                    bottom = dimensionResource(R.dimen.expanded_group_vertical_margin)
                ),
            shadowElevation = 2.dp,
        ) {

            ExpenseItem(
                expense = expense,
                onExpenseClick = onExpenseClick,
            )
        }
    }
}

@Composable
private fun CategoryItem(
    category: ExpenseCategory,
    colorIndex: Int,
    onClick: () -> Unit,
) {

    ListItem(
        textLeft = category.name,
        subTextLeft = "",

        textRight = "",
        subTextRight = annotateExpensesTotals(category.totals),

        colorIndex = colorIndex,

        onClick = onClick,
    )
}
