package com.ivzb.urgat.data.portfolio

import java.math.BigDecimal

data class Portfolio(
    val name: String,
    val category: String,
    val shares: BigDecimal,
    val marketValue: BigDecimal,
    val currency: String,
)