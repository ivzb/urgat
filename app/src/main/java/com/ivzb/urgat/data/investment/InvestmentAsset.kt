package com.ivzb.urgat.data.investment

import com.ivzb.urgat.util.UNKNOWN
import com.ivzb.urgat.util.calculatePnL
import com.ivzb.urgat.util.format
import com.ivzb.urgat.util.formatCurrency
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

    /** One unfetched quote is enough to make the summed [marketPrice] a placeholder. */
    val isMarketPriceStale: Boolean
        get() = investments.any { it.isMarketPriceStale }

    val unrealisedPnL: BigDecimal? =
        if (isMarketPriceStale) null else calculatePnL(costPrice, marketPrice)

    fun formatMarketPrice(): String =
        if (isMarketPriceStale) {
            UNKNOWN
        } else {
            "${marketPrice.format()} ${currency.formatCurrency()}"
        }

}