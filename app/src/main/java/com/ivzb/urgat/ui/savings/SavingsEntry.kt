package com.ivzb.urgat.ui.savings

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator
import com.ivzb.urgat.navigation.UrgatNavActions

fun EntryProviderScope<NavKey>.savingsEntry(navigator: Navigator, navActions: UrgatNavActions) {
    entry<SavingsRoute> {
        SavingsScreen(
            viewModel = hiltViewModel(),

            onAddSaving = { navActions.navigateToAddSaving() },
            onSavingClick = { navActions.navigateToEditSaving(it.id) },
            onBack = { navigator.goBack() },
        )
    }
}