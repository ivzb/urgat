package com.ivzb.urgat.domain.trades

import com.ivzb.urgat.data.trade.Trade
import com.ivzb.urgat.ui.trades.TradesFilterType_All

fun filterTrades(items: List<Trade>, filterType: String): List<Trade> {
    if (filterType == TradesFilterType_All) {
        return items
    }

    return items.filter { it.orderType == filterType }
}

