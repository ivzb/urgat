package com.ivzb.urgat.data.investment

import java.math.BigDecimal

data class InvestmentNetWorth(
    val marketValue: BigDecimal,
    val currency: String,
)