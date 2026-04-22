package com.ivzb.urgat.ui.finance

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator
import com.ivzb.urgat.navigation.UrgatNavActions

fun EntryProviderScope<NavKey>.financeEntry(navigator: Navigator, navActions: UrgatNavActions) {
    entry<FinanceRoute> {
        FinanceScreen(
            viewModel = hiltViewModel(),

            onAddExpense = { navActions.navigateToAddExpense() },
            onMoreExpenses = { navActions.navigateToExpenses() },
            onExpenseClick = { navActions.navigateToEditExpense(it.id) },

            onAddInvestment = { navActions.navigateToAddInvestment() },
            onMoreInvestments = { navActions.navigateToInvestments() },
            onInvestmentClick = { navActions.navigateToEditInvestment(it.id) },

            onAddTrade = { navActions.navigateToAddTrade() },
            onMoreTrades = { navActions.navigateToTrades() },
            onTradeClick = { navActions.navigateToEditTrade(it.id) },

            onAddSaving = { navActions.navigateToAddSaving() },
            onMoreSavings = { navActions.navigateToSavings() },
            onSavingClick = { navActions.navigateToEditSaving(it.id) },

            onAddBudget = { navActions.navigateToAddBudget() },
            onMoreBudgets = { navActions.navigateToBudgets() },
            onBudgetClick = { navActions.navigateToEditBudget(it.id) },

            onAddWatchlistAsset = { navActions.navigateToAddWatchlistAsset() },
            onMoreWatchlistAssets = { navActions.navigateToWatchlistAssets() },
            onWatchlistAssetClick = { navActions.navigateToEditWatchlistAsset(it.id) },

            navigator = navigator,
        )
    }
}