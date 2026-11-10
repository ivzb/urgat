package com.ivzb.urgat.ui.investments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
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
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentAverage
import com.ivzb.urgat.data.investment.InvestmentCategory
import com.ivzb.urgat.domain.investment.GroupedInvestmentCategories
import com.ivzb.urgat.ui.components.EmptyState
import com.ivzb.urgat.ui.components.FilteringUiInfo
import com.ivzb.urgat.ui.components.ListItem
import com.ivzb.urgat.ui.components.charts.PieChart
import com.ivzb.urgat.ui.components.charts.PieChartItem
import com.ivzb.urgat.util.annotateChart
import com.ivzb.urgat.util.annotateCostPrice
import com.ivzb.urgat.util.annotateMarketPrice
import com.ivzb.urgat.util.rememberLazyListStates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun GroupedCategoriesPage(
    categories: List<GroupedInvestmentCategories>,
    averages: Map<String, InvestmentAverage>,
    filteringUiInfo: FilteringUiInfo,
    expandedCategoryNames: List<String>,
    onCategoryClick: (String) -> Unit,
    onInvestmentClick: (Investment) -> Unit,
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
                total = annotateMarketPrice(category.total),

                categories = category.items,
                expandedCategoryNames = expandedCategoryNames,

                average = averages[category.group] ?: InvestmentAverage.EMPTY,

                filteringUiInfo = filteringUiInfo,
                onResetFilters = onResetFilters,

                onCategoryClick = onCategoryClick,
                onInvestmentClick = onInvestmentClick,

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
    categories: List<GroupedInvestmentCategories>,
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

    categories: List<InvestmentCategory>,
    expandedCategoryNames: List<String>,

    average: InvestmentAverage,

    filteringUiInfo: FilteringUiInfo,
    onResetFilters: () -> Unit,

    onCategoryClick: (String) -> Unit,
    onInvestmentClick: (Investment) -> Unit,

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
            onInvestmentClick = onInvestmentClick,
        )
    }
}

@Composable
private fun CategoriesChart(
    group: String,
    total: AnnotatedString?,
    categories: List<InvestmentCategory>,
    expandedCategoryNames: List<String>,
    average: InvestmentAverage,
    averagePrefix: String,
    selectedChartTitle: String?,
    onSelectedChartTitleChanged: (String?) -> Unit,
) {
    val lastExpandedName = expandedCategoryNames.lastOrNull()
    val expandedCategory = categories.firstOrNull { it.name == lastExpandedName }

    val averageTotals = expandedCategory
        ?.let { average.categories[it.name].orEmpty() }
        ?: average.totals

    // the average is what was put in per month, so it reads the cost price without its P&L
    val averageText = annotateCostPrice(averageTotals, withPnL = false, prefix = "$averagePrefix ")

    val data: List<PieChartItem>
    val defaultText: AnnotatedString

    if (expandedCategory == null) {
        // all categories pie chart
        data = categories.map {
            PieChartItem(
                value = it.investments.sumOf { it.totalMarketPrice }.toFloat(),
                title = it.name,
                total = annotateMarketPrice(it.totals),
            )
        }
        defaultText = annotateChart(group, total, averageText)
    } else {
        // expanded category pie chart
        data = expandedCategory.investments
            .groupBy { it.name }
            .map { (name, investments) ->
                val investmentTotal = Investment.total(investments)

                PieChartItem(
                    value = investments.sumOf { it.totalMarketPrice }.toFloat(),
                    title = name,
                    total = annotateMarketPrice(investmentTotal),
                )
            }
        defaultText = annotateChart(expandedCategory.name, annotateMarketPrice(expandedCategory.totals), averageText)
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
    categories: List<InvestmentCategory>,
    expandedCategoryNames: List<String>,
    onCategoryClick: (String) -> Unit,
    onInvestmentClick: (Investment) -> Unit,
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
            subInvestments(category.investments, onInvestmentClick)
        }
    }
}

@Composable
private fun CategoryItem(
    category: InvestmentCategory,
    colorIndex: Int,
    onClick: () -> Unit,
) {
    ListItem(
        textLeft = category.name,
        subTextRight = annotateMarketPrice(category.totals),
        colorIndex = colorIndex,
        onClick = onClick,
    )
}
