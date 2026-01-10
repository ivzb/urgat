package com.ivzb.urgat.ui.expenses

import androidx.annotation.VisibleForTesting
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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpenseCategory
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.domain.expense.GroupedExpenseCategories
import com.ivzb.urgat.domain.expense.GroupedExpenses
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.FilterMenu
import com.ivzb.urgat.ui.components.ItemHeaderSmall
import com.ivzb.urgat.ui.components.ListItem
import com.ivzb.urgat.ui.components.charts.PieChart
import com.ivzb.urgat.ui.components.charts.PieChartItem
import com.ivzb.urgat.ui.listDetail.ListDetailScreen
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.ui.theme.expenseColor
import com.ivzb.urgat.util.annotateFormattedString
import com.ivzb.urgat.util.format
import com.ivzb.urgat.util.isFirst
import com.ivzb.urgat.util.isLast
import com.ivzb.urgat.util.paddingIf
import com.ivzb.urgat.util.toLocalDate
import com.ivzb.urgat.util.toggle
import kotlinx.coroutines.launch
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
    val pages = EXPENSE_PAGES
    val pagerState = rememberPagerState(initialPage = uiState.initialPage) { pages.size }
    val listStates = remember { List(pages.size) { LazyListState() } }
    val currentListState = remember { mutableStateOf(listStates[0]) }

    ListDetailScreen(
        uiState = uiState,

        route = ExpensesRoute,
        onSearchQueryChanged = { query -> onIntent(ExpensesIntent.SearchQueryChanged(query)) },
        onAddItem = onAddExpense,
        onBack = onBack,

        topAppBarActionsContent = {
            FilterMenu(
                items = uiState.filteringUiInfo.filterMenuItems,
                onFilter = { onIntent(ExpensesIntent.SetFiltering(it)) },
            )
        },
        listState = currentListState.value,

        content = {
            ExpensesContent(
                uiState = uiState,
                onExpenseClick = onExpenseClick,
                pages = pages,
                pagerState = pagerState,
                listStates = listStates,
                onListStateChanged = { currentListState.value = it },
            )
        },

        modifier = modifier,
    )
}

@Composable
internal fun ExpensesContent(
    uiState: ExpensesUiState,
    onExpenseClick: (Expense) -> Unit,
    pages: List<ExpensesPage>,
    pagerState: PagerState,
    listStates: List<LazyListState>,
    onListStateChanged: (LazyListState) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val expandedMonthlyIds = rememberSaveable { mutableStateSetOf<String>() }
    val expandedYearlyIds = rememberSaveable { mutableStateSetOf<String>() }
    val expandedAllTimeIds = rememberSaveable { mutableStateSetOf<String>() }
    val isHorizontalScrollEnabled by remember {
        derivedStateOf {
            val currentPage = pages[pagerState.currentPage]
            currentPage != ExpensesPage.Monthly && currentPage != ExpensesPage.Yearly
        }
    }

    Column {
        PrimaryScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 0.dp
        ) {
            pages.forEachIndexed { index, page ->
                Tab(
                    text = {
                        Text(
                            text = stringResource(page.titleResId),
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
            userScrollEnabled = isHorizontalScrollEnabled,
            modifier = Modifier
                .fillMaxSize(),
        ) { pageIndex ->

            when (pages[pageIndex]) {
                ExpensesPage.Transactions -> TransactionsPage(
                    expenses = uiState.expenses,
                    onExpenseClick = onExpenseClick,

                    listState = listStates[pageIndex],
                    onListStateChanged = onListStateChanged,
                )

                ExpensesPage.Monthly -> GroupedCategoriesPage(
                    categories = uiState.categoriesMonthly,
                    expandedCategoryIds = expandedMonthlyIds,
                    onCategoryClick = { categoryId -> expandedMonthlyIds.toggle(categoryId) },
                    onExpenseClick = onExpenseClick,
                    onListStateChanged = onListStateChanged,
                )

                ExpensesPage.Yearly -> GroupedCategoriesPage(
                    categories = uiState.categoriesYearly,
                    expandedCategoryIds = expandedYearlyIds,
                    onCategoryClick = { categoryId -> expandedYearlyIds.toggle(categoryId) },
                    onExpenseClick = onExpenseClick,
                    onListStateChanged = onListStateChanged,
                )

                ExpensesPage.AllTime -> CategoriesPage(
                    group = stringResource(R.string.all_time),
                    categories = uiState.categoriesAllTime,
                    expandedCategoryIds = expandedAllTimeIds,
                    listState = listStates[pageIndex],
                    onCategoryClick = { categoryId -> expandedAllTimeIds.toggle(categoryId) },
                    onExpenseClick = onExpenseClick,
                    onListStateChanged = onListStateChanged,
                )
            }
        }
    }
}

@Composable
private fun TransactionsPage(
    expenses: List<GroupedExpenses>,
    onExpenseClick: (Expense) -> Unit,
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

        expenses.forEachIndexed { index, (group, total, expenses) ->
            item {
                ItemHeaderSmall(
                    text = group,
                    total = (total * -1.toBigDecimal()).format(),
                    totalFontWeight = FontWeight.Bold,
                    totalColor = expenseColor(total),
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
private fun GroupedCategoriesPage(
    categories: List<GroupedExpenseCategories>,
    expandedCategoryIds: Set<String>,
    onCategoryClick: (String) -> Unit,
    onExpenseClick: (Expense) -> Unit,
    onListStateChanged: (LazyListState) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState { categories.size }
    val nestedListStates = remember(categories.size) {
        List(categories.size) { LazyListState() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
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

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = true,
            modifier = Modifier
                .fillMaxSize(),
        ) { pageIndex ->

            val category = categories[pageIndex]

            CategoriesPage(
                group = category.group,
                categories = category.items,
                expandedCategoryIds = expandedCategoryIds,

                onCategoryClick = onCategoryClick,
                onExpenseClick = onExpenseClick,

                listState = nestedListStates[pageIndex],
                onListStateChanged = onListStateChanged,
            )
        }
    }
}

@Composable
private fun CategoriesPage(
    group: String,
    categories: List<ExpenseCategory>,
    expandedCategoryIds: Set<String>,

    onCategoryClick: (String) -> Unit,
    onExpenseClick: (Expense) -> Unit,

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
                val valueFormatted = (value * -1.toBigDecimal()).format()
                val textFormatted =
                    stringResource(R.string.chart_text_format, name, valueFormatted).trim()

                return annotateFormattedString(
                    formattedString = textFormatted,
                    part = valueFormatted,
                    spanStyle = SpanStyle(
                        color = expenseColor(value),
                        fontWeight = FontWeight.Bold
                    ),
                )
            }

            val data = categories.map {
                PieChartItem(
                    value = it.value.toFloat(),
                    selectedText = formattedText(it.name, it.value)
                )
            }
            val total = categories.sumOf { it.value }
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

        categories.forEachIndexed { index, category ->
            item(key = category.id) {
                CategoryItem(
                    category = category,
                    colorIndex = index,
                    onClick = { onCategoryClick(category.id) },
                )
            }

            val isExpanded = expandedCategoryIds.contains(category.id)

            if (isExpanded) {
                subExpenses(category.expenses, onExpenseClick)
            }
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
fun ExpenseItem(
    expense: Expense,
    onExpenseClick: (Expense) -> Unit
) {
    val date = remember { expense.date.toLocalDate() }

    ListItem(
        textLeft = date.dayOfMonth.toString(),
        subTextLeft = date.month.name.take(3),

        textRight = expense.name,
        subTextRight = (expense.amount * -1.toBigDecimal()).format(),

        subTextRightFontWeight = FontWeight.Bold,
        subTextRightColor = expenseColor(expense.amount),

        onClick = { onExpenseClick(expense) }
    )
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
        subTextRight = (category.value * -1.toBigDecimal()).format(),
        subTextRightFontWeight = FontWeight.Bold,
        subTextRightColor = expenseColor(category.value),

        colorIndex = colorIndex,

        onClick = onClick,
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
                filteringUiInfo = ExpensesFilteringUiInfo(),
                isLoading = false,
                hasError = false,
                initialPage = initialPage,
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
                expenses = listOf(),
                categoriesMonthly = listOf(),
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
                expenses = listOf(),
                categoriesMonthly = listOf(),
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
