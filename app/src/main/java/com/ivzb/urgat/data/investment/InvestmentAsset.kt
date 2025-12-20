package com.ivzb.urgat.data.investment

import java.math.BigDecimal

data class InvestmentAsset(
    val name: String,
    val category: String,
    val marketValue: BigDecimal,
    val currency: String,
)