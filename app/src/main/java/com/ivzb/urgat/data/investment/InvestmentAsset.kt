package com.ivzb.urgat.data.investment

import java.math.BigDecimal

data class InvestmentAsset(
    val id: String,
    val name: String,
    val category: String,
    val shares: BigDecimal,
    val marketValue: BigDecimal,
    val currency: String,

    val investments: List<Investment>,
)