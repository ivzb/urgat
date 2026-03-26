package com.ivzb.urgat.data.investment

import com.ivzb.urgat.util.format
import java.math.BigDecimal

data class InvestmentTotal(
    val currency: String,
    val costPrice: BigDecimal,
    val marketPrice: BigDecimal,
    val pnl: BigDecimal?,
) {

    companion object {

        const val SEPARATOR = " | "

        fun List<InvestmentTotal>.formatCostPrice(): String = format(InvestmentTotal::costPrice)

        fun List<InvestmentTotal>.formatMarketPrice(): String = format(InvestmentTotal::marketPrice)

        private fun List<InvestmentTotal>.format(total: (InvestmentTotal) -> BigDecimal): String =
            joinToString(separator = SEPARATOR) { "${total(it).format()} ${it.currency}" }

    }

}