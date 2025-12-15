package com.ivzb.urgat.ui.expenses

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator
import com.ivzb.urgat.navigation.UrgatNavActions

fun EntryProviderScope<NavKey>.expensesEntry(navigator: Navigator, navActions: UrgatNavActions) {
    entry<ExpensesRoute> {
        ExpensesScreen(
            viewModel = hiltViewModel(),

            onAddExpense = { navActions.navigateToAddExpense() },
            onExpenseClick = { navActions.navigateToEditExpense(it.id) },
            onBack = { navigator.goBack() },
        )
    }
}