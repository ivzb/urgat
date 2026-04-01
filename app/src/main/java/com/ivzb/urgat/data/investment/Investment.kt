package com.ivzb.urgat.data.investment

import com.ivzb.urgat.util.calculatePnL
import java.math.BigDecimal

data class Investment(
    val id: String,
    val name: String,
    val shares: BigDecimal,
    val costPrice: BigDecimal,
    val marketPrice: BigDecimal,
    val category: String,
    val currency: String,
    val date: Long,
    val isDeleted: Boolean,
    val createdAt: Long,
    val description: String? = null,
) {

    val totalCostPrice: BigDecimal
        get() = costPrice * shares

    val totalMarketPrice: BigDecimal
        get() = marketPrice * shares

    val unrealisedPnL: BigDecimal? = calculatePnL(totalCostPrice, totalMarketPrice)

    fun formatCsv(): String =
        "\"$id\", \"$name\", \"$shares\", \"$costPrice\", \"$category\", \"$currency\", $date, $createdAt"

    companion object {

        fun total(investments: List<Investment>): List<InvestmentTotal> =
            investments
                .groupBy { it.currency.uppercase() }
                .map { (currency, investments) ->
                    val costPrice = investments.sumOf { it.totalCostPrice }
                    val marketPrice = investments.sumOf { it.totalMarketPrice }

                    InvestmentTotal(
                        currency = currency,
                        costPrice = costPrice,
                        marketPrice = marketPrice,
                        pnl = calculatePnL(costPrice, marketPrice),
                    )
                }
                .sortedBy { it.currency }

        suspend fun total(
            investments: List<Investment>?,
            toCurrency: String,
            convert: suspend (amount: BigDecimal, fromCurrency: String, toCurrency: String) -> BigDecimal?,
        ): BigDecimal =
            investments
                ?.mapNotNull { convert(it.totalCostPrice, it.currency, toCurrency) }
                ?.sumOf { it }
                ?: BigDecimal.ZERO

    }

}