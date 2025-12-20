package com.ivzb.urgat.domain.common

import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentAsset
import com.ivzb.urgat.data.investment.InvestmentNetWorth
import com.ivzb.urgat.ui.GroupedItems
import com.ivzb.urgat.util.timestamp
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class GroupTest {

    @Test
    fun byDate() {
        val items = listOf(
            GroupItemTest("item1", timestamp(2025, 12, 20)),
            GroupItemTest("item2", timestamp(2025, 12, 20)),
            GroupItemTest("item3", timestamp(2025, 11, 20)),
            GroupItemTest("item4", timestamp(2025, 12, 5)),
            GroupItemTest("item5", timestamp(2025, 10, 9)),
        )

        val expected = listOf(
            GroupedItems(
                group = "December 2025",
                items = listOf(
                    GroupItemTest(name = "item1", date = 1766181600000),
                    GroupItemTest(name = "item2", date = 1766181600000),
                    GroupItemTest(name = "item4", date = 1764885600000),
                ),
            ),
            GroupedItems(
                group = "November 2025",
                items = listOf(
                    GroupItemTest(name = "item3", date = 1763589600000)
                )
            ),
            GroupedItems(
                group = "October 2025",
                items = listOf(
                    GroupItemTest(name = "item5", date = 1759957200000)
                )
            )
        )

        val actual = Group.byDate(items, GroupItemTest::date)

        assertEquals(expected, actual)
    }

    @Test
    fun groupInvestmentAssets() {
        val investments = listOf(
            Investment("1", "GOOGL", BigDecimal("2.1"), BigDecimal("307.16"), "stocks", "USD", timestamp(2025, 12, 20), false, timestamp(2025, 12, 20)),
            Investment("2", "AAPL", BigDecimal("2.3"), BigDecimal("273.67"), "stocks", "USD", timestamp(2025, 12, 19), false, timestamp(2025, 12, 19)),
            Investment("3", "NVDA", BigDecimal("2.5"), BigDecimal("180.99"), "stocks", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("4", "SNP 500", BigDecimal("0.3"), BigDecimal("6834.5"), "etf", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("5", "BRK.B", BigDecimal("1.7"), BigDecimal("494.53"), "etf", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("6", "Gold", BigDecimal("1"), BigDecimal("4338.55"), "gold", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("7", "GOOGL", BigDecimal("2.1"), BigDecimal("262.25"), "stocks", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("8", "SNP 500", BigDecimal("0.3"), BigDecimal("5835.3"), "etf", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("9", "Gold", BigDecimal("1"), BigDecimal("3704.25"), "gold", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
        )

        val expected = listOf(
            InvestmentAsset(category = "etf", amount = BigDecimal("2891.051"), currency = "USD"),
            InvestmentAsset(category = "etf", amount = BigDecimal("1750.59"), currency = "EUR"),
            InvestmentAsset(category = "gold", amount = BigDecimal("4338.55"), currency = "USD"),
            InvestmentAsset(category = "gold", amount = BigDecimal("3704.25"), currency = "EUR"),
            InvestmentAsset(category = "stocks", amount = BigDecimal("1726.952"), currency = "USD"),
            InvestmentAsset(category = "stocks", amount = BigDecimal("550.725"), currency = "EUR"),
        )

        val actual = Group.groupInvestmentAssets(investments)

        assertEquals(expected, actual)
    }

    @Test
    fun groupInvestmentNetWorth() {
        val investments = listOf(
            Investment("1", "GOOGL", BigDecimal("2.1"), BigDecimal("307.16"), "stocks", "USD", timestamp(2025, 12, 20), false, timestamp(2025, 12, 20)),
            Investment("2", "AAPL", BigDecimal("2.3"), BigDecimal("273.67"), "stocks", "USD", timestamp(2025, 12, 19), false, timestamp(2025, 12, 19)),
            Investment("3", "NVDA", BigDecimal("2.5"), BigDecimal("180.99"), "stocks", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("4", "SNP 500", BigDecimal("0.3"), BigDecimal("6834.5"), "etf", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("5", "BRK.B", BigDecimal("1.7"), BigDecimal("494.53"), "etf", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("6", "Gold", BigDecimal("1"), BigDecimal("4338.55"), "gold", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("7", "GOOGL", BigDecimal("2.1"), BigDecimal("262.25"), "stocks", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("8", "SNP 500", BigDecimal("0.3"), BigDecimal("5835.3"), "etf", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("9", "Gold", BigDecimal("1"), BigDecimal("3704.25"), "gold", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
        )

        val expected = listOf(
            InvestmentNetWorth(amount = BigDecimal("6005.565"), currency = "EUR"),
            InvestmentNetWorth(amount=BigDecimal("8956.553"), currency="USD"),
        )

        val actual = Group.groupInvestmentNetWorth(investments)

        assertEquals(expected, actual)
    }

    companion object {

        private data class GroupItemTest(
            val name: String,
            val date: Long,
        )

    }

}