package com.ivzb.urgat.data.investment

import com.ivzb.urgat.util.format
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

    companion object {

        fun total(investments: List<Investment>): List<String> =
            investments
                .groupBy { it.currency }
                .map { (currency, investments) ->
                    val marketValue = investments.sumOf { it.marketValue }.format()
                    "$marketValue $currency"
                }

    }

}