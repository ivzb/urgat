package com.ivzb.urgat.ui.finance

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator
import com.ivzb.urgat.navigation.UrgatNavActions
import com.ivzb.urgat.navigation.navigateToAddBudget
import com.ivzb.urgat.navigation.navigateToAddExpense
import com.ivzb.urgat.navigation.navigateToAddInvestment
import com.ivzb.urgat.navigation.navigateToAddSaving
import com.ivzb.urgat.navigation.navigateToAddTrade
import com.ivzb.urgat.navigation.navigateToAddWatchlistAsset
import com.ivzb.urgat.navigation.navigateToBudgets
import com.ivzb.urgat.navigation.navigateToEditBudget
import com.ivzb.urgat.navigation.navigateToEditExpense
import com.ivzb.urgat.navigation.navigateToEditInvestment
import com.ivzb.urgat.navigation.navigateToEditSaving
import com.ivzb.urgat.navigation.navigateToEditTrade
import com.ivzb.urgat.navigation.navigateToEditWatchlistAsset
import com.ivzb.urgat.navigation.navigateToExpenses
import com.ivzb.urgat.navigation.navigateToInvestments
import com.ivzb.urgat.navigation.navigateToSavings
import com.ivzb.urgat.navigation.navigateToTrades

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
            onWatchlistAssetClick = { navActions.navigateToEditWatchlistAsset(it.id) },

            navigator = navigator,
        )
    }
}
