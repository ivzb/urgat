package com.ivzb.urgat.data.portfolio

import com.ivzb.urgat.util.div
import java.math.BigDecimal

data class Portfolio(
    val name: String,
    val category: String,
    val shares: BigDecimal,
    val costValue: BigDecimal?,
    val marketValue: BigDecimal,
    val currency: String,
) {

    val diffPercent: BigDecimal? = costValue
        ?.takeIf { it.compareTo(BigDecimal.ZERO) != 0 }
        ?.let { (marketValue - it) / it * BigDecimal(100) }

}
