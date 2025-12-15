package com.ivzb.urgat.ui.investments

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator
import com.ivzb.urgat.navigation.UrgatNavActions

fun EntryProviderScope<NavKey>.investmentsEntry(navigator: Navigator, navActions: UrgatNavActions) {
    entry<InvestmentsRoute> {
        InvestmentsScreen(
            viewModel = hiltViewModel(),

            onAddInvestment = { navActions.navigateToAddInvestment() },
            onInvestmentClick = { navActions.navigateToEditInvestment(it.id) },
            onBack = { navigator.goBack() },
        )
    }
}