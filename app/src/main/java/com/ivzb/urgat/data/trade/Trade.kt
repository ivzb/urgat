package com.ivzb.urgat.data.trade

import com.ivzb.urgat.util.formatCurrency
import java.math.BigDecimal
import kotlin.collections.component1
import kotlin.collections.component2

data class Trade(
    val id: String,
    val name: String,
    val shares: BigDecimal,
    val marketPrice: BigDecimal,
    val fee: BigDecimal,
    val currency: String,
    val orderType: String,
    val date: Long,
    val isDeleted: Boolean,
    val createdAt: Long,
    val pricePrecision: Int? = null,
) {

    val totalPrice: BigDecimal
        get() = (marketPrice * shares) + fee

    fun formatCsv(): String =
        "\"$id\", \"$name\", \"$shares\", \"$marketPrice\", \"$fee\", \"$currency\", \"$orderType\", $date, $createdAt"

    companion object {

        fun total(trades: List<Trade>): List<TradeTotal> =
            trades
                .groupBy { it.currency.formatCurrency() }
                .map { (currency, trades) ->
                    val price = trades.sumOf { it.totalPrice }

                    TradeTotal(
                        currency = currency,
                        price = price,
                    )
                }
                .sortedBy { it.currency }

        suspend fun total(
            trades: List<Trade>?,
            toCurrency: String,
            convert: suspend (amount: BigDecimal, fromCurrency: String, toCurrency: String) -> BigDecimal?,
        ): BigDecimal =
            trades
                ?.mapNotNull { convert(it.totalPrice, it.currency, toCurrency) }
                ?.sumOf { it }
                ?: BigDecimal.ZERO

    }

}