package com.ivzb.urgat.ui.budgets

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator

fun EntryProviderScope<NavKey>.budgetsEntry(navigator: Navigator) {
    entry<BudgetsRoute> {
        BudgetsScreen(
            viewModel = hiltViewModel(),

            onBack = { navigator.goBack() },
        )
    }
}