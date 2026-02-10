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
            onExpenseClick = { navActions.navigateToEditExpense(it.id) },

            onAddInvestment = { navActions.navigateToAddInvestment() },
            onInvestmentClick = { navActions.navigateToEditInvestment(it.id) },

            onAddSaving = { navActions.navigateToAddSaving() },
            onSavingClick = { navActions.navigateToEditSaving(it.id) },

            onAddBudget = { navActions.navigateToAddBudget() },
            onBudgetClick = { navActions.navigateToEditBudget(it.id) },

            navigator = navigator,
        )
    }
}