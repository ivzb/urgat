package com.ivzb.urgat.ui.fuelConsumption

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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ivzb.urgat.R
import com.ivzb.urgat.data.fuelConsumption.FuelConsumption
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionCategory
import com.ivzb.urgat.domain.fuelConsumption.GroupedFuelConsumptionCategories
import com.ivzb.urgat.ui.components.EmptyState
import com.ivzb.urgat.ui.components.FilteringUiInfo
import com.ivzb.urgat.ui.components.ListItem
import com.ivzb.urgat.ui.components.charts.PieChart
import com.ivzb.urgat.ui.components.charts.PieChartItem
import com.ivzb.urgat.util.annotateChart
import com.ivzb.urgat.util.annotateFuelConsumptionTotals
import com.ivzb.urgat.util.isFirst
import com.ivzb.urgat.util.isLast
import com.ivzb.urgat.util.paddingIf
import com.ivzb.urgat.util.rememberLazyListStates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun GroupedCategoriesPage(
    categories: List<GroupedFuelConsumptionCategories>,
    filteringUiInfo: FilteringUiInfo,
    expandedCategoryIds: List<String>,
    onCategoryClick: (String) -> Unit,
    onFuelConsumptionClick: (FuelConsumption) -> Unit,
    onResetFilters: () -> Unit,
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
                total = annotateFuelConsumptionTotals(category.total),

                categories = category.items,
                expandedCategoryIds = expandedCategoryIds,

                filteringUiInfo = filteringUiInfo,
                onResetFilters = onResetFilters,

                onCategoryClick = onCategoryClick,
                onFuelConsumptionClick = onFuelConsumptionClick,

                listState = nestedListStates[pageIndex],
                onListStateChanged = onListStateChanged,
            )
        }
    }
}

@Composable
private fun CategoriesTabRow(
    categories: List<GroupedFuelConsumptionCategories>,
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

    categories: List<FuelConsumptionCategory>,
    expandedCategoryIds: List<String>,

    filteringUiInfo: FilteringUiInfo,
    onResetFilters: () -> Unit,

    onCategoryClick: (String) -> Unit,
    onFuelConsumptionClick: (FuelConsumption) -> Unit,

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
                expandedCategoryIds = expandedCategoryIds,
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
            expandedCategoryIds = expandedCategoryIds,
            onCategoryClick = onCategoryClick,
            onFuelConsumptionClick = onFuelConsumptionClick,
        )
    }
}

@Composable
private fun CategoriesChart(
    group: String,
    total: AnnotatedString?,
    categories: List<FuelConsumptionCategory>,
    expandedCategoryIds: List<String>,
) {
    val lastExpandedId = expandedCategoryIds.lastOrNull()
    val expandedCategory = categories.firstOrNull { it.id == lastExpandedId }

    val data: List<PieChartItem>
    val defaultText: AnnotatedString

    if (expandedCategory == null) {
        // all categories pie chart
        data = categories.map {
            PieChartItem(
                value = it.fuelConsumption.sumOf { it.amount }.toFloat(),
                title = it.name,
                total = annotateFuelConsumptionTotals(it.totals),
            )
        }
        defaultText = annotateChart(group, total)
    } else {
        // expanded category pie chart
        data = expandedCategory.fuelConsumption
            .groupBy { it.type }
            .map { (name, fuelConsumption) ->
                val fuelConsumptionTotal = FuelConsumption.total(fuelConsumption)

                PieChartItem(
                    value = fuelConsumption.sumOf { it.amount }.toFloat(),
                    title = name,
                    total = annotateFuelConsumptionTotals(fuelConsumptionTotal),
                )
            }
        defaultText = annotateChart(expandedCategory.name, annotateFuelConsumptionTotals(expandedCategory.totals))
    }

    PieChart(
        data = data,
        defaultText = defaultText,
        textStyle = MaterialTheme.typography.titleLarge,
    )
}

private fun LazyListScope.categoryItems(
    categories: List<FuelConsumptionCategory>,
    expandedCategoryIds: List<String>,
    onCategoryClick: (String) -> Unit,
    onFuelConsumptionClick: (FuelConsumption) -> Unit,
) {
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
            subFuelConsumption(category.fuelConsumption, onFuelConsumptionClick)
        }
    }
}

private fun LazyListScope.subFuelConsumption(
    fuelConsumption: List<FuelConsumption>,
    onFuelConsumptionClick: (FuelConsumption) -> Unit,
) {
    itemsIndexed(
        fuelConsumption,
        key = { _, fuelConsumption -> fuelConsumption.id },
    ) { index, item ->

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.horizontal_margin))
                .paddingIf(
                    condition = index.isFirst(),
                    top = dimensionResource(R.dimen.expanded_group_vertical_margin)
                )
                .paddingIf(
                    condition = index.isLast(fuelConsumption.size),
                    bottom = dimensionResource(R.dimen.expanded_group_vertical_margin)
                ),
            shadowElevation = 2.dp,
        ) {

            FuelConsumptionItem(
                fuelConsumption = item,
                onFuelConsumptionClick = onFuelConsumptionClick,
            )
        }
    }
}

@Composable
private fun CategoryItem(
    category: FuelConsumptionCategory,
    colorIndex: Int,
    onClick: () -> Unit,
) {

    ListItem(
        textLeft = category.name,
        subTextLeft = "",

        textRight = "",
        subTextRight = annotateFuelConsumptionTotals(category.totals),

        colorIndex = colorIndex,

        onClick = onClick,
    )
}
