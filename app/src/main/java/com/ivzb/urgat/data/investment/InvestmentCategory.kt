package com.ivzb.urgat.data.investment

data class InvestmentCategory(
    val id: String,
    val name: String,
    val totals: List<InvestmentTotal>,

    val investments: List<Investment>,
)