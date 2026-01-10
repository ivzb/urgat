package com.ivzb.urgat.data.investment

import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.util.format
import com.ivzb.urgat.util.toInt
import java.math.BigDecimal

data class Investment(
    val id: String,
    val name: String,
    val shares: BigDecimal,
    val cost: BigDecimal,
    val category: String,
    val currency: String,
    val date: Long,
    val isDeleted: Boolean,
    val createdAt: Long
) {

    val marketValue: BigDecimal
        get() = cost * shares

    fun formatCsv(): String =
        "\"$id\", \"$name\", \"$shares\", \"$cost\", \"$category\", \"$currency\", $date, $createdAt"

    companion object {

        const val SEPARATOR = " | "

        fun total(investments: List<Investment>): String =
            investments
                .groupBy { it.currency }
                .map { (currency, investments) ->
                    val marketValue = investments.sumOf { it.marketValue }.format()
                    val total = "$marketValue $currency"
                    currency to total
                }
                .sortedBy { (currency, _) -> currency }
                .joinToString(SEPARATOR) { (_, total) -> total }

    }

}