package com.ivzb.urgat.ui.investments

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator
import com.ivzb.urgat.navigation.UrgatNavActions
import com.ivzb.urgat.navigation.navigateToEditInvestment

fun EntryProviderScope<NavKey>.investmentsEntry(navigator: Navigator, navActions: UrgatNavActions) {
    entry<InvestmentsRoute> {
        InvestmentsScreen(
            viewModel = hiltViewModel(),

            onInvestmentClick = { navActions.navigateToEditInvestment(it.id) },
            onBack = { navigator.goBack() },
        )
    }
}
