package com.ivzb.urgat.ui.savings

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
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.saving.SavingTotal.Companion.sumByCurrency
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.domain.saving.GroupedSavings
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.ItemHeaderSmall
import com.ivzb.urgat.ui.components.ListItem
import com.ivzb.urgat.ui.components.MoreMenu
import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.components.SortMenu
import com.ivzb.urgat.ui.components.screens.PagerScreen
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.util.annotateSavings
import com.ivzb.urgat.util.annotateSavingsTotals
import com.ivzb.urgat.util.format
import com.ivzb.urgat.util.paddingIf
import com.ivzb.urgat.util.toLocalDate
import com.ivzb.urgat.util.toggle

@Composable
fun SavingsScreen(
    viewModel: SavingsViewModel,

    onSavingClick: (Saving) -> Unit,
    onBack: () -> Unit,

    modifier: Modifier = Modifier,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SavingsScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onSavingClick = onSavingClick,
        onBack = onBack,
        modifier = modifier
    )
}

@VisibleForTesting
@Composable
fun SavingsScreen(
    uiState: SavingsUiState,
    onIntent: (SavingsIntent) -> Unit,

    onSavingClick: (Saving) -> Unit,

    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val expandedMonthlyIds = rememberSaveable { mutableStateSetOf<String>() }
    val expandedYearlyIds = rememberSaveable { mutableStateSetOf<String>() }
    val expandedAllTimeIds = rememberSaveable { mutableStateSetOf<String>() }

    PagerScreen(
        uiState = uiState,

        route = SavingsRoute,
        searchQuery = uiState.filteringUiInfo.searchQuery,
        onSearchQueryChanged = { query -> onIntent(SavingsIntent.SearchQueryChanged(query)) },
        onBack = onBack,

        topAppBarActionsContent = {
            SavingsTopAppBarActions(
                uiState = uiState,
                onIntent = onIntent,
            )
        },

        content = { page, listState, onListStateChanged ->
            SavingsPageContent(
                page = page,
                uiState = uiState,
                onIntent = onIntent,
                expandedMonthlyIds = expandedMonthlyIds,
                expandedYearlyIds = expandedYearlyIds,
                expandedAllTimeIds = expandedAllTimeIds,
                onSavingClick = onSavingClick,
                listState = listState,
                onListStateChanged = onListStateChanged,
            )
        },

        modifier = modifier,
    )
}

@Composable
private fun SavingsTopAppBarActions(
    uiState: SavingsUiState,
    onIntent: (SavingsIntent) -> Unit,
) {
    SortMenu(
        items = uiState.filteringUiInfo.sortMenuItems,
        showBadge = uiState.filteringUiInfo.showSortBadge,
        onFilter = { onIntent(SavingsIntent.SetSorting(it)) },
    )

    MoreMenu(
        listOfNotNull(
            (R.string.reset_filters to { onIntent(SavingsIntent.ResetFilters) }).takeIf { uiState.filteringUiInfo.hasAppliedFilters },
        )
    )
}

@Composable
private fun SavingsPageContent(
    page: Page,
    uiState: SavingsUiState,
    onIntent: (SavingsIntent) -> Unit,
    expandedMonthlyIds: MutableSet<String>,
    expandedYearlyIds: MutableSet<String>,
    expandedAllTimeIds: MutableSet<String>,
    onSavingClick: (Saving) -> Unit,
    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {
    when (page) {
        SavingsPage.Monthly -> GroupedCategoriesPage(
            categories = uiState.categoriesMonthly,
            filteringUiInfo = uiState.filteringUiInfo,
            expandedCategoryIds = expandedMonthlyIds,
            onCategoryClick = { categoryId -> expandedMonthlyIds.toggle(categoryId) },
            onSavingClick = onSavingClick,
            onResetFilters = { onIntent(SavingsIntent.ResetFilters) },
            onListStateChanged = onListStateChanged,
        )

        SavingsPage.Yearly -> GroupedCategoriesPage(
            categories = uiState.categoriesYearly,
            filteringUiInfo = uiState.filteringUiInfo,
            expandedCategoryIds = expandedYearlyIds,
            onCategoryClick = { categoryId -> expandedYearlyIds.toggle(categoryId) },
            onSavingClick = onSavingClick,
            onResetFilters = { onIntent(SavingsIntent.ResetFilters) },
            onListStateChanged = onListStateChanged,
        )

        SavingsPage.AllTime -> CategoriesPage(
            group = stringResource(R.string.all_time),
            total = annotateSavingsTotals(
                uiState.categoriesAllTime
                    .flatMap { it.totals }
                    .sumByCurrency()
            ),
            categories = uiState.categoriesAllTime,
            expandedCategoryIds = expandedAllTimeIds,

            filteringUiInfo = uiState.filteringUiInfo,
            onResetFilters = { onIntent(SavingsIntent.ResetFilters) },

            listState = listState,
            onCategoryClick = { categoryId -> expandedAllTimeIds.toggle(categoryId) },
            onSavingClick = onSavingClick,
            onListStateChanged = onListStateChanged,
        )
    }
}

@Composable
fun TransactionsPage(
    savings: List<GroupedSavings>,
    onSavingClick: (Saving) -> Unit,

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

        savings.forEachIndexed { index, (group, total, savings) ->
            item {
                ItemHeaderSmall(
                    text = group,
                    total = annotateSavingsTotals(total),
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
fun SavingItem(
    saving: Saving,
    onSavingClick: (Saving) -> Unit
) {
    val date = remember { saving.date.toLocalDate() }

    ListItem(
        textLeft = date.dayOfMonth.toString(),
        subTextLeft = date.month.name.take(3),

        textRight = saving.name,
        subTextRight = annotateSavings(saving.amount, saving.currency),

        onClick = { onSavingClick(saving) }
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
            onIntent = {},
            onSavingClick = {},
            onBack = {},
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
            onIntent = {},
            onSavingClick = {},
            onBack = {},
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
            onIntent = {},
            onSavingClick = {},
            onBack = {},
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
            onIntent = {},
            onSavingClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}
