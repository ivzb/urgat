package com.ivzb.urgat.domain.common

import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentAsset
import com.ivzb.urgat.data.investment.InvestmentCategory
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
            GroupItemTest("item1", "EUR", timestamp(2025, 12, 20)),
            GroupItemTest("item2", "USD", timestamp(2025, 12, 20)),
            GroupItemTest("item3", "EUR", timestamp(2025, 11, 20)),
            GroupItemTest("item4", "USD", timestamp(2025, 12, 5)),
            GroupItemTest("item5", "USD", timestamp(2025, 10, 9)),
        )

        val expected = listOf(
            GroupedItems(
                group = "December 2025",
                total = "EUR | USD",
                items = listOf(
                    GroupItemTest(name = "item1", currency = "EUR", date = 1766181600000),
                    GroupItemTest(name = "item2", currency = "USD", date = 1766181600000),
                    GroupItemTest(name = "item4", currency = "USD", date = 1764885600000),
                ),
            ),
            GroupedItems(
                group = "November 2025",
                total = "EUR",
                items = listOf(
                    GroupItemTest(name = "item3", currency = "EUR", date = 1763589600000)
                )
            ),
            GroupedItems(
                group = "October 2025",
                total = "USD",
                items = listOf(
                    GroupItemTest(name = "item5", currency = "USD", date = 1759957200000)
                )
            )
        )

        val actual = Group.byDate(items, GroupItemTest::date, GroupItemTest::total)

        assertEquals(expected, actual)
    }

    @Test
    fun groupInvestmentAssets() {
        val investments = listOf(
            Investment("1", "GOOGL", BigDecimal("2.1"), BigDecimal("307.16"), "stocks", "USD", timestamp(2025, 12, 20), false, timestamp(2025, 12, 20)),
            Investment("2", "GOOGL", BigDecimal("2.1"), BigDecimal("307.16"), "stocks", "USD", timestamp(2025, 12, 20), false, timestamp(2025, 12, 20)),
            Investment("3", "AAPL", BigDecimal("2.3"), BigDecimal("273.67"), "stocks", "USD", timestamp(2025, 12, 19), false, timestamp(2025, 12, 19)),
            Investment("4", "AAPL", BigDecimal("2.3"), BigDecimal("273.67"), "stocks", "USD", timestamp(2025, 12, 19), false, timestamp(2025, 12, 19)),
            Investment("5", "NVDA", BigDecimal("2.5"), BigDecimal("180.99"), "stocks", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("6", "NVDA", BigDecimal("2.5"), BigDecimal("180.99"), "stocks", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("7", "SNP 500", BigDecimal("0.3"), BigDecimal("6834.5"), "etf", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("8", "SNP 500", BigDecimal("0.3"), BigDecimal("6834.5"), "etf", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("9", "BRK.B", BigDecimal("1.7"), BigDecimal("494.53"), "etf", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("10", "BRK.B", BigDecimal("1.7"), BigDecimal("494.53"), "etf", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("11", "Gold", BigDecimal("1"), BigDecimal("4338.55"), "gold", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("12", "Gold", BigDecimal("1"), BigDecimal("4338.55"), "gold", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("13", "GOOGL", BigDecimal("2.1"), BigDecimal("262.25"), "stocks", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("14", "GOOGL", BigDecimal("2.1"), BigDecimal("262.25"), "stocks", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("15", "SNP 500", BigDecimal("0.3"), BigDecimal("5835.3"), "etf", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("16", "SNP 500", BigDecimal("0.3"), BigDecimal("5835.3"), "etf", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("17", "Gold", BigDecimal("1"), BigDecimal("3704.25"), "gold", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("18", "Gold", BigDecimal("1"), BigDecimal("3704.25"), "gold", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
        )

        val expected = listOf(
            InvestmentAsset(name="AAPL", category = "stocks", shares = BigDecimal("4.6"), marketValue = BigDecimal("1258.882"), currency="USD"),
            InvestmentAsset(name="BRK.B", category = "etf", shares = BigDecimal("3.4"), marketValue = BigDecimal("1681.402"), currency="USD"),
            InvestmentAsset(name="GOOGL", category = "stocks", shares = BigDecimal("4.2"), marketValue = BigDecimal("1290.072"), currency="USD"),
            InvestmentAsset(name="GOOGL", category = "stocks", shares = BigDecimal("4.2"), marketValue = BigDecimal("1101.450"), currency="EUR"),
            InvestmentAsset(name="Gold", category = "gold", shares = BigDecimal("2"), marketValue = BigDecimal("8677.10"), currency="USD"),
            InvestmentAsset(name="Gold", category = "gold", shares = BigDecimal("2"), marketValue = BigDecimal("7408.50"), currency="EUR"),
            InvestmentAsset(name="NVDA", category = "stocks", shares = BigDecimal("5.0"), marketValue = BigDecimal("904.950"), currency="USD"),
            InvestmentAsset(name="SNP 500", category = "etf", shares = BigDecimal("0.6"), marketValue = BigDecimal("4100.70"), currency="USD"),
            InvestmentAsset(name="SNP 500", category = "etf", shares = BigDecimal("0.6"), marketValue = BigDecimal("3501.18"), currency="EUR"),
        )

        val actual = Group.groupInvestmentAssets(investments)

        assertEquals(expected, actual)
    }

    @Test
    fun groupInvestmentCategories() {
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
            InvestmentCategory(name = "etf", values = listOf("2,891.05 USD", "1,750.59 EUR")),
            InvestmentCategory(name = "gold", values = listOf("4,338.55 USD", "3,704.25 EUR")),
            InvestmentCategory(name = "stocks", values = listOf("1,726.95 USD", "550.72 EUR")),
        )

        val actual = Group.groupInvestmentCategories(investments)

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

        val expected = InvestmentNetWorth(values = listOf("8,956.55 USD", "6,005.56 EUR"))
        val actual = Group.groupInvestmentNetWorth(investments)

        assertEquals(expected, actual)
    }

    companion object {

        private data class GroupItemTest(
            val name: String,
            val currency: String,
            val date: Long,
        ) {

            companion object {

                fun total(items: List<GroupItemTest>) = items
                    .map { it.currency }
                    .sorted()
                    .distinct()
                    .joinToString(" | ")

            }

        }

    }

}