package com.ivzb.urgat.ui.budgets

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator
import com.ivzb.urgat.navigation.UrgatNavActions

fun EntryProviderScope<NavKey>.budgetsEntry(navigator: Navigator, navActions: UrgatNavActions) {
    entry<BudgetsRoute> {
        BudgetsScreen(
            viewModel = hiltViewModel(),

            onAddBudget = { navActions.navigateToAddBudget() },
            onBack = { navigator.goBack() },
        )
    }
}