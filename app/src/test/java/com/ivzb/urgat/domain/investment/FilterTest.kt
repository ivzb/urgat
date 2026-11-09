package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.Investment
import java.math.BigDecimal
import kotlin.test.assertEquals
import org.junit.Test

class FilterTest {

    @Test
    fun `excludeClosedPositions drops positions fully sold over multiple transactions`() {
        val investments = listOf(
            investment("1", "MSFT", "2.527", "stock", "EUR"),
            investment("2", "MSFT", "0.611", "stock", "EUR"),
            investment("3", "MSFT", "2.527", "stock", "EUR"),
            investment("4", "MSFT", "-5.054", "stock", "EUR"),
            investment("5", "MSFT", "-0.611", "stock", "EUR"),
            investment("6", "AAPL", "1", "stock", "EUR"),
        )

        assertEquals(listOf("6"), excludeClosedPositions(investments).map { it.id })
    }

    @Test
    fun `excludeClosedPositions keeps partially sold positions`() {
        val investments = listOf(
            investment("1", "MSFT", "2.527", "stock", "EUR"),
            investment("2", "MSFT", "-1", "stock", "EUR"),
        )

        assertEquals(investments, excludeClosedPositions(investments))
    }

    @Test
    fun `excludeClosedPositions tells positions apart by name, category and currency`() {
        val investments = listOf(
            investment("1", "MSFT", "1", "stock", "EUR"),
            investment("2", "MSFT", "-1", "stock", "EUR"),
            investment("3", "MSFT", "1", "stock", "USD"),
            investment("4", "MSFT", "-1", "etf", "EUR"),
        )

        assertEquals(listOf("3", "4"), excludeClosedPositions(investments).map { it.id })
    }

    private fun investment(id: String, name: String, shares: String, category: String, currency: String) =
        Investment(id, name, BigDecimal(shares), BigDecimal("100"), BigDecimal("110"), category, currency, 1L, false, 1L)

}
