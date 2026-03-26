package com.ivzb.urgat.data.investment

import com.ivzb.urgat.util.calculatePnL
import java.math.BigDecimal

data class InvestmentAsset(
    val id: String,
    val name: String,
    val category: String,
    val shares: BigDecimal,
    val costPrice: BigDecimal,
    val marketPrice: BigDecimal,
    val currency: String,
    val totals: List<InvestmentTotal>,

    val investments: List<Investment>,
) {

    val unrealisedPnL: BigDecimal? = calculatePnL(costPrice, marketPrice)

}