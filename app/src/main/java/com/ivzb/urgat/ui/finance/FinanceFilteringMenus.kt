package com.ivzb.urgat.ui.finance

import androidx.compose.runtime.Composable
import com.ivzb.urgat.ui.components.FilterMenu
import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.components.SortMenu

/**
 * The filter menu shown in the top app bar of the given finance [page], if it can be filtered.
 */
@Composable
fun FinanceFilterMenu(
    page: Page,
    uiState: FinanceUiState,
    onIntent: (FinanceIntent) -> Unit,
) {
    when (page) {
        FinancePage.Expenses -> FilterMenu(
            items = uiState.expensesUiState.filteringUiInfo.filterMenuItems,
            showBadge = uiState.expensesUiState.filteringUiInfo.showFilterBadge,
            onFilter = { onIntent(FinanceIntent.SetExpensesFiltering(it)) },
        )

        FinancePage.Investments -> FilterMenu(
            items = uiState.investmentsUiState.filteringUiInfo.filterMenuItems,
            showBadge = uiState.investmentsUiState.filteringUiInfo.showFilterBadge,
            onFilter = { onIntent(FinanceIntent.SetInvestmentsFiltering(it)) },
        )

        FinancePage.Trades -> FilterMenu(
            items = uiState.tradesUiState.filteringUiInfo.filterMenuItems,
            showBadge = uiState.tradesUiState.filteringUiInfo.showFilterBadge,
            onFilter = { onIntent(FinanceIntent.SetTradesFiltering(it)) },
        )

        FinancePage.Budgets -> FilterMenu(
            items = uiState.budgetsUiState.filteringUiInfo.filterMenuItems,
            showBadge = uiState.budgetsUiState.filteringUiInfo.showFilterBadge,
            onFilter = { onIntent(FinanceIntent.SetBudgetsFiltering(it)) },
        )
    }
}

/**
 * The sort menu shown in the top app bar of the given finance [page], if it can be sorted.
 */
@Composable
fun FinanceSortMenu(
    page: Page,
    uiState: FinanceUiState,
    onIntent: (FinanceIntent) -> Unit,
) {
    when (page) {
        FinancePage.Expenses -> SortMenu(
            items = uiState.expensesUiState.filteringUiInfo.sortMenuItems,
            showBadge = uiState.expensesUiState.filteringUiInfo.showSortBadge,
            onFilter = { onIntent(FinanceIntent.SetExpensesSorting(it)) },
        )

        FinancePage.Investments -> SortMenu(
            items = uiState.investmentsUiState.filteringUiInfo.sortMenuItems,
            showBadge = uiState.investmentsUiState.filteringUiInfo.showSortBadge,
            onFilter = { onIntent(FinanceIntent.SetInvestmentsSorting(it)) },
        )

        FinancePage.Trades -> SortMenu(
            items = uiState.tradesUiState.filteringUiInfo.sortMenuItems,
            showBadge = uiState.tradesUiState.filteringUiInfo.showSortBadge,
            onFilter = { onIntent(FinanceIntent.SetTradesSorting(it)) },
        )

        FinancePage.Savings -> SortMenu(
            items = uiState.savingsUiState.filteringUiInfo.sortMenuItems,
            showBadge = uiState.savingsUiState.filteringUiInfo.showSortBadge,
            onFilter = { onIntent(FinanceIntent.SetSavingsSorting(it)) },
        )

        FinancePage.Budgets -> SortMenu(
            items = uiState.budgetsUiState.filteringUiInfo.sortMenuItems,
            showBadge = uiState.budgetsUiState.filteringUiInfo.showSortBadge,
            onFilter = { onIntent(FinanceIntent.SetBudgetsSorting(it)) },
        )

        FinancePage.Watchlist -> SortMenu(
            items = uiState.watchlistAssetsUiState.filteringUiInfo.sortMenuItems,
            showBadge = uiState.watchlistAssetsUiState.filteringUiInfo.showSortBadge,
            onFilter = { onIntent(FinanceIntent.SetWatchlistAssetsSorting(it)) },
        )
    }
}
