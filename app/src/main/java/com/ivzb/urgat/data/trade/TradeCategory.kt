package com.ivzb.urgat.data.trade

data class TradeCategory(
    val id: String,
    val name: String,
    val totals: List<TradeTotal>,

    val trades: List<Trade>,
)

