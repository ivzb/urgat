package com.ivzb.urgat.data.investment

import com.ivzb.urgat.util.UNKNOWN
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class InvestmentMappingTest {

    @Test
    fun toExternal_withMarketPrice() {
        val investment = entity().toExternal(
            description = "Apple Inc.",
            marketPrice = "150".toBigDecimal(),
            pricePrecision = 2,
        )

        assertEquals("150".toBigDecimal(), investment.marketPrice)
        assertFalse(investment.isMarketPriceStale)
    }

    @Test
    fun toExternal_withoutMarketPrice() {
        val investment = entity().toExternal(
            description = null,
            marketPrice = null,
            pricePrecision = null,
        )

        // the position still has to be valued somehow, so it falls back to what it cost
        assertEquals("100".toBigDecimal(), investment.marketPrice)

        // but that fallback is never passed off as a real quote
        assertTrue(investment.isMarketPriceStale)
    }

    @Test
    fun toExternal_withoutMarketPrice_hasNoProfitAndLoss() {
        val investment = entity().toExternal(
            description = null,
            marketPrice = null,
            pricePrecision = null,
        )

        // cost == market only because the quote is missing, which would otherwise read as a
        // confident break even
        assertNull(investment.unrealisedPnL)
    }

    @Test
    fun toExternal_withoutMarketPrice_formatsAsUnknown() {
        val investment = entity().toExternal(
            description = null,
            marketPrice = null,
            pricePrecision = null,
        )

        assertEquals(UNKNOWN, investment.formatTotalMarketPrice())
    }

    @Test
    fun toExternal_withMarketPrice_formatsTotal() {
        val investment = entity(shares = "2").toExternal(
            description = null,
            marketPrice = "150".toBigDecimal(),
            pricePrecision = 2,
        )

        assertEquals("300.00 EUR", investment.formatTotalMarketPrice())
    }

    private fun entity(
        shares: String = "1",
        cost: String = "100",
    ) = InvestmentEntity(
        id = "1",
        name = "AAPL",
        shares = shares.toBigDecimal(),
        cost = cost.toBigDecimal(),
        category = "Stocks",
        currency = "eur",
        date = 1L,
        isDeleted = false,
        createdAt = 1L,
    )
}

