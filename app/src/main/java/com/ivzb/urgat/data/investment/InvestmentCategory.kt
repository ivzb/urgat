package com.ivzb.urgat.data.investment

data class InvestmentCategory(
    val id: String,
    val name: String,
    val total: String,

    val investments: List<Investment>,
)