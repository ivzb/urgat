package com.ivzb.urgat.data.watchlist

import java.math.BigDecimal
import java.math.RoundingMode

data class WatchlistAsset(
    val id: String,
    val name: String,
    val description: String?,
    val marketPrice: BigDecimal?,
    val currency: String?,
    val previousClosePrice: BigDecimal?,
    val pricePrecision: Int?,
    val isDeleted: Boolean,
    val createdAt: Long
) {

    val priceChange: BigDecimal? by lazy {
        previousClosePrice?.let { marketPrice?.minus(it) }
    }

    val priceChangePercentage: BigDecimal? by lazy {
        marketPrice?.takeIf { it != BigDecimal.ZERO }?.let {
            priceChange
                ?.divide(marketPrice, 10, RoundingMode.HALF_UP)
                ?.multiply(BigDecimal(100))
        }
    }

    fun formatCsv(): String =
        "\"$id\", \"$name\", $createdAt"

}