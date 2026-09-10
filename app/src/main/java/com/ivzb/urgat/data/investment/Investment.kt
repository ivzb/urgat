package com.ivzb.urgat.data.investment

import com.ivzb.urgat.util.UNKNOWN
import com.ivzb.urgat.util.calculatePnL
import com.ivzb.urgat.util.div
import com.ivzb.urgat.util.format
import com.ivzb.urgat.util.formatCurrency
import com.ivzb.urgat.util.toLocalDate
import java.math.BigDecimal
import java.time.YearMonth

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
    val pricePrecision: Int? = null,

    /**
     * `true` when no quote could be fetched and [marketPrice] fell back to [costPrice].
     *
     * The position then values at exactly what it cost, which is indistinguishable from a real
     * break even - hence the flag, so totals built on top can say they are incomplete instead of
     * quietly reporting the wrong number.
     */
    val isMarketPriceStale: Boolean = false,
) {

    val totalCostPrice: BigDecimal
        get() = costPrice * shares

    val totalMarketPrice: BigDecimal
        get() = marketPrice * shares

    // a stale quote makes the market price a copy of the cost price, so the P&L would always
    // read as a confident break even - it is unknown instead
    val unrealisedPnL: BigDecimal? =
        if (isMarketPriceStale) null else calculatePnL(totalCostPrice, totalMarketPrice)

    fun formatTotalMarketPrice(): String =
        if (isMarketPriceStale) {
            UNKNOWN
        } else {
            "${totalMarketPrice.format(digits = pricePrecision)} ${currency.formatCurrency()}"
        }

    fun formatCsv(): String =
        "\"$id\", \"$name\", \"$shares\", \"$costPrice\", \"$category\", \"$currency\", $date, $createdAt"

    companion object {

        fun total(investments: List<Investment>): List<InvestmentTotal> =
            investments
                .groupBy { it.currency.formatCurrency() }
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

        /**
         * Amount of distinct months the given [investments] span over.
         */
        fun months(investments: List<Investment>): Int =
            investments
                .map { YearMonth.from(it.date.toLocalDate()) }
                .distinct()
                .size

        /**
         * Average monthly totals per currency of the given [investments],
         * spread over [months] (defaults to the months the [investments] span over).
         *
         * Both prices are spread over the same months, so the P&L of the average
         * reads the same as the P&L of the period it was taken from.
         */
        fun averageMonthly(
            investments: List<Investment>,
            months: Int = months(investments),
        ): List<InvestmentTotal> =
            if (months <= 0) {
                listOf()
            } else {
                total(investments).map { total ->
                    val costPrice = total.costPrice / months.toBigDecimal()
                    val marketPrice = total.marketPrice / months.toBigDecimal()

                    InvestmentTotal(
                        currency = total.currency,
                        costPrice = costPrice,
                        marketPrice = marketPrice,
                        pnl = calculatePnL(costPrice, marketPrice),
                    )
                }
            }

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