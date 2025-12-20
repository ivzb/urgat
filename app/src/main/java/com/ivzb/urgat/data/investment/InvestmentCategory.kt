package com.ivzb.urgat.data.investment

import java.math.BigDecimal

data class InvestmentCategory(
    val name: String,
    val marketValue: BigDecimal,
    val currency: String,
)