package com.ivzb.urgat.data.investment

import com.ivzb.urgat.util.timestamp
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class InvestmentAverageMonthlyTest {

    @Test
    fun `months should count distinct months`() {
        val investments = listOf(
            investment(id = "1", costPrice = "100", marketPrice = "100", date = timestamp(2025, 1, 5)),
            investment(id = "2", costPrice = "100", marketPrice = "100", date = timestamp(2025, 1, 25)),
            investment(id = "3", costPrice = "100", marketPrice = "100", date = timestamp(2025, 2, 3)),
            investment(id = "4", costPrice = "100", marketPrice = "100", date = timestamp(2026, 1, 3)),
        )

        assertEquals(3, Investment.months(investments))
    }

    @Test
    fun `months should be zero when there are no investments`() {
        assertEquals(0, Investment.months(listOf()))
    }

    @Test
    fun `averageMonthly should divide totals by the months the investments span over`() {
        val investments = listOf(
            investment(id = "1", costPrice = "100", marketPrice = "110", date = timestamp(2025, 1, 5)),
            investment(id = "2", costPrice = "200", marketPrice = "220", date = timestamp(2025, 2, 3)),
            investment(id = "3", costPrice = "300", marketPrice = "330", date = timestamp(2025, 3, 3)),
        )

        assertEquals(
            listOf(
                InvestmentTotal(
                    currency = "EUR",
                    costPrice = BigDecimal("200.00"),
                    marketPrice = BigDecimal("220.00"),
                    pnl = BigDecimal("10.00"),
                ),
            ),
            Investment.averageMonthly(investments),
        )
    }

    @Test
    fun `averageMonthly should divide totals by the given months`() {
        val investments = listOf(
            investment(id = "1", costPrice = "100", marketPrice = "110", date = timestamp(2025, 1, 5)),
            investment(id = "2", costPrice = "200", marketPrice = "220", date = timestamp(2025, 2, 3)),
        )

        assertEquals(
            listOf(
                InvestmentTotal(
                    currency = "EUR",
                    costPrice = BigDecimal("25.00"),
                    marketPrice = BigDecimal("27.50"),
                    pnl = BigDecimal("10.00"),
                ),
            ),
            Investment.averageMonthly(investments, months = 12),
        )
    }

    @Test
    fun `averageMonthly should be calculated per currency`() {
        val investments = listOf(
            investment(id = "1", costPrice = "100", marketPrice = "110", currency = "EUR", date = timestamp(2025, 1, 5)),
            investment(id = "2", costPrice = "300", marketPrice = "330", currency = "USD", date = timestamp(2025, 2, 3)),
        )

        assertEquals(
            listOf(
                InvestmentTotal(
                    currency = "EUR",
                    costPrice = BigDecimal("50.00"),
                    marketPrice = BigDecimal("55.00"),
                    pnl = BigDecimal("10.00"),
                ),
                InvestmentTotal(
                    currency = "USD",
                    costPrice = BigDecimal("150.00"),
                    marketPrice = BigDecimal("165.00"),
                    pnl = BigDecimal("10.00"),
                ),
            ),
            Investment.averageMonthly(investments),
        )
    }

    @Test
    fun `averageMonthly should be empty when there are no investments`() {
        assertEquals(listOf(), Investment.averageMonthly(listOf()))
    }

    @Test
    fun `averageMonthly should be empty when months are not positive`() {
        val investments = listOf(
            investment(id = "1", costPrice = "100", marketPrice = "110", date = timestamp(2025, 1, 5)),
        )

        assertEquals(listOf(), Investment.averageMonthly(investments, months = 0))
    }

    private fun investment(
        id: String,
        costPrice: String,
        marketPrice: String,
        date: Long,
        shares: String = "1",
        currency: String = "EUR",
        category: String = "Stocks",
    ) = Investment(
        id = id,
        name = "investment $id",
        shares = BigDecimal(shares),
        costPrice = BigDecimal(costPrice),
        marketPrice = BigDecimal(marketPrice),
        category = category,
        currency = currency,
        date = date,
        isDeleted = false,
        createdAt = date,
    )
}

