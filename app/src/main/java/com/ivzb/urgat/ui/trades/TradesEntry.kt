package com.ivzb.urgat.ui.trades

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator
import com.ivzb.urgat.navigation.UrgatNavActions
import com.ivzb.urgat.navigation.navigateToEditTrade

fun EntryProviderScope<NavKey>.tradesEntry(navigator: Navigator, navActions: UrgatNavActions) {
    entry<TradesRoute> {
        TradesScreen(
            viewModel = hiltViewModel(),

            onTradeClick = { navActions.navigateToEditTrade(it.id) },
            onBack = { navigator.goBack() },
        )
    }
}
