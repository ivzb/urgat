package com.ivzb.urgat.ui.savings

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
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ivzb.urgat.R
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.saving.SavingCategory
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.domain.saving.GroupedSavingCategories
import com.ivzb.urgat.domain.saving.GroupedSavings
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.ItemHeaderSmall
import com.ivzb.urgat.ui.components.ListItem
import com.ivzb.urgat.ui.components.charts.PieChart
import com.ivzb.urgat.ui.components.charts.PieChartItem
import com.ivzb.urgat.ui.components.format.formatSavingsChart
import com.ivzb.urgat.ui.components.screens.PagerScreen
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.ui.theme.savingColor
import com.ivzb.urgat.util.format
import com.ivzb.urgat.util.isFirst
import com.ivzb.urgat.util.isLast
import com.ivzb.urgat.util.paddingIf
import com.ivzb.urgat.util.toLocalDate
import com.ivzb.urgat.util.toggle
import kotlinx.coroutines.launch

@Composable
fun SavingsScreen(
    uiState: SavingsUiState,
    onSavingClick: (Saving) -> Unit,
    modifier: Modifier = Modifier,
) {

    val expandedMonthlyIds = rememberSaveable { mutableStateSetOf<String>() }
    val expandedYearlyIds = rememberSaveable { mutableStateSetOf<String>() }
    val expandedAllTimeIds = rememberSaveable { mutableStateSetOf<String>() }

    PagerScreen(
        uiState = uiState,

        content = { page, listState, onListStateChanged ->
            when (page) {
                SavingsPage.Transactions -> TransactionsPage(
                    savings = uiState.savings,
                    onSavingClick = onSavingClick,

                    listState = listState,
                    onListStateChanged = onListStateChanged,
                )

                SavingsPage.Monthly -> GroupedCategoriesPage(
                    categories = uiState.categoriesMonthly,
                    expandedCategoryIds = expandedMonthlyIds,
                    onCategoryClick = { categoryId -> expandedMonthlyIds.toggle(categoryId) },
                    onSavingClick = onSavingClick,
                    onListStateChanged = onListStateChanged,
                )

                SavingsPage.Yearly -> GroupedCategoriesPage(
                    categories = uiState.categoriesYearly,
                    expandedCategoryIds = expandedYearlyIds,
                    onCategoryClick = { categoryId -> expandedYearlyIds.toggle(categoryId) },
                    onSavingClick = onSavingClick,
                    onListStateChanged = onListStateChanged,
                )

                SavingsPage.AllTime -> CategoriesPage(
                    group = stringResource(R.string.all_time),
                    categories = uiState.categoriesAllTime,
                    expandedCategoryIds = expandedAllTimeIds,
                    listState = listState,
                    onCategoryClick = { categoryId -> expandedAllTimeIds.toggle(categoryId) },
                    onSavingClick = onSavingClick,
                    onListStateChanged = onListStateChanged,
                )
            }
        },

        modifier = modifier,
    )
}

@Composable
private fun TransactionsPage(
    savings: List<GroupedSavings>,
    onSavingClick: (Saving) -> Unit,
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

        savings.forEachIndexed { index, (group, total, savings) ->
            item {
                ItemHeaderSmall(
                    text = group,
                    total = total.format(),
                    totalFontWeight = FontWeight.Bold,
                    totalColor = savingColor(total),
                    modifier = Modifier
                        .paddingIf(index == 0, bottom = dimensionResource(R.dimen.vertical_margin))
                        .paddingIf(index > 0, vertical = dimensionResource(R.dimen.vertical_margin))
                        .padding(horizontal = dimensionResource(R.dimen.list_item_padding))
                )
            }

            items(
                items = savings,
                key = { saving -> saving.id },
            ) { saving ->
                SavingItem(
                    saving = saving,
                    onSavingClick = onSavingClick
                )
            }
        }
    }
}

@Composable
private fun GroupedCategoriesPage(
    categories: List<GroupedSavingCategories>,
    expandedCategoryIds: Set<String>,
    onCategoryClick: (String) -> Unit,
    onSavingClick: (Saving) -> Unit,
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
                onSavingClick = onSavingClick,

                listState = nestedListStates[pageIndex],
                onListStateChanged = onListStateChanged,
            )
        }
    }
}

@Composable
private fun CategoriesPage(
    group: String,
    categories: List<SavingCategory>,
    expandedCategoryIds: Set<String>,

    onCategoryClick: (String) -> Unit,
    onSavingClick: (Saving) -> Unit,

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
            val data = categories.map {
                PieChartItem(
                    value = it.value.toFloat(),
                    selectedText = formatSavingsChart(it.name, it.value)
                )
            }
            val total = categories.sumOf { it.value }
            val defaultText = formatSavingsChart(group, total)

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
                subSavings(category.savings, onSavingClick)
            }
        }
    }
}

private fun LazyListScope.subSavings(
    savings: List<Saving>,
    onSavingClick: (Saving) -> Unit,
) {
    itemsIndexed(
        savings,
        key = { _, saving -> saving.id },
    ) { index, saving ->

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.horizontal_margin))
                .paddingIf(
                    condition = index.isFirst(),
                    top = dimensionResource(R.dimen.expanded_group_vertical_margin)
                )
                .paddingIf(
                    condition = index.isLast(savings.size),
                    bottom = dimensionResource(R.dimen.expanded_group_vertical_margin)
                ),
            shadowElevation = 2.dp,
        ) {

            SavingItem(
                saving = saving,
                onSavingClick = onSavingClick,
            )
        }
    }
}

@Composable
fun SavingItem(
    saving: Saving,
    onSavingClick: (Saving) -> Unit
) {
    val date = remember { saving.date.toLocalDate() }

    ListItem(
        textLeft = date.dayOfMonth.toString(),
        subTextLeft = date.month.name.take(3),

        textRight = saving.name,
        subTextRight = saving.amount.format(),

        subTextRightFontWeight = FontWeight.Bold,
        subTextRightColor = savingColor(saving.amount),

        onClick = { onSavingClick(saving) }
    )
}

@Composable
private fun CategoryItem(
    category: SavingCategory,
    colorIndex: Int,
    onClick: () -> Unit,
) {
    ListItem(
        textLeft = category.name,
        subTextLeft = "",

        textRight = "",
        subTextRight = category.value.format(),
        subTextRightFontWeight = FontWeight.Bold,
        subTextRightColor = savingColor(category.value),

        colorIndex = colorIndex,

        onClick = onClick,
    )
}

@AllDevicesPreview
@Composable
private fun SavingsScreenPreview(initialPage: Int = 0) {
    UrgatTheme {
        SavingsScreen(
            uiState = SavingsUiState(
                savings = SampleData.SAMPLE_SAVINGS_GROUPED,
                categoriesMonthly = SampleData.SAMPLE_SAVINGS_CATEGORIES_MONTHLY,
                categoriesYearly = SampleData.SAMPLE_SAVINGS_CATEGORIES_YEARLY,
                categoriesAllTime = SampleData.SAMPLE_SAVINGS_CATEGORIES_ALL_TIME,
                filteringUiInfo = SavingsFilteringUiInfo(),
                isLoading = false,
                hasError = false,
                initialPage = initialPage,
            ),
            onSavingClick = {},
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun SavingsScreenMonthlyPreview() {
    SavingsScreenPreview(initialPage = 1)
}

@AllDevicesPreview
@Composable
private fun SavingsScreenYearlyPreview() {
    SavingsScreenPreview(initialPage = 2)
}

@AllDevicesPreview
@Composable
private fun SavingsScreenAllTimePreview() {
    SavingsScreenPreview(initialPage = 3)
}

@AllDevicesPreview
@Composable
private fun SavingsScreenEmptyPreview() {
    UrgatTheme {
        SavingsScreen(
            uiState = SavingsUiState(
                savings = listOf(),
                filteringUiInfo = SavingsFilteringUiInfo(),
                isLoading = false,
                hasError = false,
            ),
            onSavingClick = {},
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun SavingsScreenLoadingPreview() {
    UrgatTheme {
        SavingsScreen(
            uiState = SavingsUiState(
                isLoading = true,
            ),
            onSavingClick = {},
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun SavingsScreenErrorPreview() {
    UrgatTheme {
        SavingsScreen(
            uiState = SavingsUiState(
                hasError = true,
            ),
            onSavingClick = {},
            modifier = Modifier
        )
    }
}
