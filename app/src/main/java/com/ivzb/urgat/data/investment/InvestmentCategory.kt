package com.ivzb.urgat.data.investment

data class InvestmentCategory(
    val id: String,
    val name: String,
    val values: List<String>,

    val investments: List<Investment>,
)