package com.ivzb.urgat.data.investment

import java.math.BigDecimal

data class InvestmentTotal(
    val currency: String,
    val costPrice: BigDecimal,
    val marketPrice: BigDecimal,
    val pnl: BigDecimal?,
)