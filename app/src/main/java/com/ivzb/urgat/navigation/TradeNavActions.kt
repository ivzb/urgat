package com.ivzb.urgat.navigation

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.addEditTrade.AddEditTradeRoute
import com.ivzb.urgat.ui.trades.TradesRoute

fun UrgatNavActions.navigateToTrades() {
    navigator.navigate(TradesRoute)
}

fun UrgatNavActions.navigateToAddTrade() {
    navigator.navigate(
        AddEditTradeRoute(
            id = null,
            titleResId = R.string.add_trade
        )
    )
}

fun UrgatNavActions.navigateToEditTrade(tradeId: String) {
    navigator.navigate(
        AddEditTradeRoute(
            id = tradeId,
            titleResId = R.string.edit_trade
        )
    )
}
