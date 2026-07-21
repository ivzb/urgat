package com.ivzb.urgat.domain.trades

import com.ivzb.urgat.data.trade.Trade
import com.ivzb.urgat.data.trade.TradeTotal
import com.ivzb.urgat.ui.components.GroupedItems

typealias GroupedTrades = GroupedItems<String, List<TradeTotal>, Trade>

