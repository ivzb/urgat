package com.ivzb.urgat.data.investment

import java.math.BigDecimal

data class InvestmentNetWorth(
    val amount: BigDecimal,
    val currency: String,
)