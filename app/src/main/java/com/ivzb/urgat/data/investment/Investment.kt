package com.ivzb.urgat.data.investment

import com.ivzb.urgat.util.format
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
    val createdAt: Long
) {

    val totalCostPrice: BigDecimal
        get() = costPrice * shares

    val totalMarketPrice: BigDecimal
        get() = marketPrice * shares

    fun formatCsv(): String =
        "\"$id\", \"$name\", \"$shares\", \"$costPrice\", \"$category\", \"$currency\", $date, $createdAt"

    companion object {

        const val SEPARATOR = " | "

        fun total(investments: List<Investment>): String =
            investments
                .groupBy { it.currency }
                .map { (currency, investments) ->
                    val totalMarketPrice = investments.sumOf { it.totalMarketPrice }.format()
                    val total = "$totalMarketPrice $currency"
                    currency to total
                }
                .sortedBy { (currency, _) -> currency }
                .joinToString(SEPARATOR) { (_, total) -> total }

    }

}