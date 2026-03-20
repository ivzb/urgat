package com.ivzb.urgat.data.marketPrice

import java.math.BigDecimal

data class MarketPrice(
    val symbol: String,
    val price: BigDecimal,
    val currency: String,
)

