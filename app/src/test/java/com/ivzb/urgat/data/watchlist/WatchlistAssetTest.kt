package com.ivzb.urgat.data.watchlist

import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class WatchlistAssetTest {

    @Test
    fun formatMarketPrice() {
        val asset = watchlistAsset(
            marketPrice = "123.456",
            previousClosePrice = "120",
        )

        // the extra fractions are cut off, never rounded up
        assertEquals("123.45 USD", asset.formatMarketPrice())
    }

    @Test
    fun formatMarketPrice_pricePrecision() {
        val asset = watchlistAsset(
            marketPrice = "123.456",
            previousClosePrice = "120",
            pricePrecision = 4,
        )

        assertEquals("123.4560 USD", asset.formatMarketPrice())
    }

    @Test
    fun formatPriceChange_up() {
        val asset = watchlistAsset(
            marketPrice = "123.45",
            previousClosePrice = "120",
            pricePrecision = 2,
        )

        assertEquals("+3.45 (+2.79%)", asset.formatPriceChange())
    }

    @Test
    fun formatPriceChange_down() {
        val asset = watchlistAsset(
            marketPrice = "100",
            previousClosePrice = "110",
            pricePrecision = 2,
        )

        assertEquals("-10.00 (-10%)", asset.formatPriceChange())
    }

    @Test
    fun formatPriceChange_unchanged() {
        val asset = watchlistAsset(
            marketPrice = "100",
            previousClosePrice = "100",
            pricePrecision = 2,
        )

        assertEquals("+0.00 (+0%)", asset.formatPriceChange())
    }

    private fun watchlistAsset(
        marketPrice: String?,
        previousClosePrice: String?,
        pricePrecision: Int? = null,
        currency: String = "usd",
    ) = WatchlistAsset(
        id = "1",
        name = "aapl",
        description = "Apple Inc.",
        marketPrice = marketPrice?.toBigDecimal(),
        currency = currency,
        previousClosePrice = previousClosePrice?.toBigDecimal(),
        pricePrecision = pricePrecision,
        isDeleted = false,
        createdAt = 1L,
    )
}


