package com.ivzb.urgat.data.trade

import java.math.BigDecimal

data class TradeTotal(
    val currency: String,
    val price: BigDecimal,
)