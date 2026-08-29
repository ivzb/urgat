package com.ivzb.urgat.data.watchlist

import com.ivzb.urgat.util.UNKNOWN
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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

    @Test
    fun formatMarketPrice_withoutPrice() {
        val asset = watchlistAsset(
            marketPrice = null,
            previousClosePrice = null,
            currency = null,
        )

        // never the literal "null null" a string template would produce
        assertEquals(UNKNOWN, asset.formatMarketPrice())
    }

    @Test
    fun formatPriceChange_withoutPrice() {
        val asset = watchlistAsset(
            marketPrice = null,
            previousClosePrice = null,
            currency = null,
        )

        assertEquals(UNKNOWN, asset.formatPriceChange())
    }

    @Test
    fun formatMarketPrice_withoutCurrency() {
        val asset = watchlistAsset(
            marketPrice = "123.45",
            previousClosePrice = "120",
            currency = null,
        )

        // a price without its currency is not something to show either
        assertEquals(UNKNOWN, asset.formatMarketPrice())
    }

    @Test
    fun formatPriceChange_withoutPreviousClose() {
        val asset = watchlistAsset(
            marketPrice = "123.45",
            previousClosePrice = null,
        )

        // there is nothing to compare against, so there is no change to show
        assertEquals(UNKNOWN, asset.formatPriceChange())
    }

    @Test
    fun formatPriceChange_zeroPrice() {
        val asset = watchlistAsset(
            marketPrice = "0",
            previousClosePrice = "10",
            pricePrecision = 2,
        )

        // the percentage is undefined against a zero price, the absolute change still is not
        assertEquals("-10.00", asset.formatPriceChange())
    }

    @Test
    fun hasMarketPrice() {
        assertTrue(watchlistAsset(marketPrice = "1", previousClosePrice = "1").hasMarketPrice)

        assertFalse(
            watchlistAsset(marketPrice = null, previousClosePrice = null, currency = null)
                .hasMarketPrice
        )
    }

    private fun watchlistAsset(
        marketPrice: String?,
        previousClosePrice: String?,
        pricePrecision: Int? = null,
        currency: String? = "usd",
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


