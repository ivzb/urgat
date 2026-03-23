package com.ivzb.urgat.data.investment

import com.ivzb.urgat.util.div
import java.math.BigDecimal

data class InvestmentAsset(
    val id: String,
    val name: String,
    val category: String,
    val shares: BigDecimal,
    val costPrice: BigDecimal,
    val marketPrice: BigDecimal,
    val currency: String,
    val total: String,

    val investments: List<Investment>,
) {

    val unrealisedPnL: BigDecimal? = costPrice
        .takeIf { it.compareTo(BigDecimal.ZERO) != 0 }
        ?.let { (marketPrice - it) / it * BigDecimal(100) }

}