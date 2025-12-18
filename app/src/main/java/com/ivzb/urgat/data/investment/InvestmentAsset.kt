package com.ivzb.urgat.data.investment

import java.math.BigDecimal

data class InvestmentAsset(
    val category: String,
    val amount: BigDecimal,
    val currency: String,
)