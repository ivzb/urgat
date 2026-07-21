package com.ivzb.urgat.domain.trades

import com.ivzb.urgat.data.trade.Trade
import com.ivzb.urgat.ui.trades.TradesSortType

fun sortTrades(items: List<Trade>, sortType: TradesSortType): List<Trade> {
    return when (sortType) {
        TradesSortType.BY_DATE -> items.sortedByDescending { it.date }
    }
}

