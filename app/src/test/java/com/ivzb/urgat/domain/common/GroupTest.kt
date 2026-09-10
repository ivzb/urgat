package com.ivzb.urgat.domain.common

import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.budget.BudgetCategory
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpenseAverage
import com.ivzb.urgat.data.expense.ExpenseCategory
import com.ivzb.urgat.data.expense.ExpenseTotal
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentAsset
import com.ivzb.urgat.data.investment.InvestmentAverage
import com.ivzb.urgat.data.investment.InvestmentCategory
import com.ivzb.urgat.data.investment.InvestmentNetWorth
import com.ivzb.urgat.data.investment.InvestmentTotal
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.saving.SavingCategory
import com.ivzb.urgat.data.saving.SavingTotal
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.util.calculatePnL
import com.ivzb.urgat.util.timestamp
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import org.junit.Test
import kotlin.test.assertEquals

class GroupTest {

    @Test
    fun byDateMonthly() {
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

        val actual = Group.byDateMonthly(items, GroupItemTest::date, GroupItemTest::total)

        assertEquals(expected, actual)
    }

    @Test
    fun byDateYearly() {
        val items = listOf(
            GroupItemTest("item1", "EUR", timestamp(2025, 12, 20)),
            GroupItemTest("item2", "USD", timestamp(2025, 12, 20)),
            GroupItemTest("item3", "EUR", timestamp(2025, 11, 20)),
            GroupItemTest("item4", "USD", timestamp(2025, 12, 5)),
            GroupItemTest("item5", "USD", timestamp(2025, 10, 9)),
            GroupItemTest("item6", "EUR", timestamp(2024, 12, 20)),
            GroupItemTest("item7", "USD", timestamp(2024, 12, 20)),
        )

        val expected = listOf(
            GroupedItems(
                group = "2025",
                total = "EUR | USD",
                items = listOf(
                    GroupItemTest(name = "item1", currency = "EUR", date = 1766181600000),
                    GroupItemTest(name = "item2", currency = "USD", date = 1766181600000),
                    GroupItemTest(name = "item3", currency = "EUR", date = 1763589600000),
                    GroupItemTest(name = "item4", currency = "USD", date = 1764885600000),
                    GroupItemTest(name = "item5", currency = "USD", date = 1759957200000),
                ),
            ),
            GroupedItems(
                group = "2024",
                total = "EUR | USD",
                items = listOf(
                    GroupItemTest(name = "item6", currency = "EUR", date = 1734645600000),
                    GroupItemTest(name = "item7", currency = "USD", date = 1734645600000),
                )
            ),
        )

        val actual = Group.byDateYearly(items, GroupItemTest::date, GroupItemTest::total)

        assertEquals(expected, actual)
    }

    @Test
    fun groupInvestmentAssets() = runBlocking {
        val investments = listOf(
            Investment("1", "GOOGL", BigDecimal("2.1"), BigDecimal("307.16"), BigDecimal("307.16"), "stocks", "USD", timestamp(2025, 12, 20), false, timestamp(2025, 12, 20)),
            Investment("2", "GOOGL", BigDecimal("2.1"), BigDecimal("307.16"), BigDecimal("307.16"), "stocks", "USD", timestamp(2025, 12, 20), false, timestamp(2025, 12, 20)),
            Investment("3", "AAPL", BigDecimal("2.3"), BigDecimal("273.67"), BigDecimal("273.67"), "stocks", "USD", timestamp(2025, 12, 19), false, timestamp(2025, 12, 19)),
            Investment("4", "AAPL", BigDecimal("2.3"), BigDecimal("273.67"), BigDecimal("273.67"), "stocks", "USD", timestamp(2025, 12, 19), false, timestamp(2025, 12, 19)),
            Investment("5", "NVDA", BigDecimal("2.5"), BigDecimal("180.99"), BigDecimal("180.99"), "stocks", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("6", "NVDA", BigDecimal("2.5"), BigDecimal("180.99"), BigDecimal("180.99"), "stocks", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("7", "SNP 500", BigDecimal("0.3"), BigDecimal("6834.5"), BigDecimal("6834.5"), "etf", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("8", "SNP 500", BigDecimal("0.3"), BigDecimal("6834.5"), BigDecimal("6834.5"), "etf", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("9", "BRK.B", BigDecimal("1.7"), BigDecimal("494.53"), BigDecimal("494.53"), "etf", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("10", "BRK.B", BigDecimal("1.7"), BigDecimal("494.53"), BigDecimal("494.53"), "etf", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("11", "Gold", BigDecimal("1"), BigDecimal("4338.55"), BigDecimal("4338.55"), "gold", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("12", "Gold", BigDecimal("1"), BigDecimal("4338.55"), BigDecimal("4338.55"), "gold", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("13", "GOOGL", BigDecimal("2.1"), BigDecimal("262.25"), BigDecimal("262.25"), "stocks", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("14", "GOOGL", BigDecimal("2.1"), BigDecimal("262.25"), BigDecimal("262.25"), "stocks", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("15", "SNP 500", BigDecimal("0.3"), BigDecimal("5835.3"), BigDecimal("5835.3"), "etf", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("16", "SNP 500", BigDecimal("0.3"), BigDecimal("5835.3"), BigDecimal("5835.3"), "etf", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("17", "Gold", BigDecimal("1"), BigDecimal("3704.25"), BigDecimal("3704.25"), "gold", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("18", "Gold", BigDecimal("1"), BigDecimal("3704.25"), BigDecimal("3704.25"), "gold", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
        )

        val expected = listOf(
            InvestmentAsset(
                id = TEST_UUID,
                name = "AAPL",
                category = "stocks",
                shares = BigDecimal("4.6"),
                costPrice = BigDecimal("1258.882"),
                marketPrice = BigDecimal("1258.882"),
                currency = "USD",
                totals = listOf(
                    InvestmentTotal("USD", BigDecimal("1258.882"), BigDecimal("1258.882"), calculatePnL(BigDecimal("1258.882"), BigDecimal("1258.882"))),
                ),
                investments = listOf(investments[2], investments[3]),
            ),
            InvestmentAsset(
                id = TEST_UUID,
                name = "BRK.B",
                category = "etf",
                shares = BigDecimal("3.4"),
                costPrice = BigDecimal("1681.402"),
                marketPrice = BigDecimal("1681.402"),
                currency = "USD",
                totals = listOf(
                    InvestmentTotal("USD", BigDecimal("1681.402"), BigDecimal("1681.402"), calculatePnL(BigDecimal("1681.402"), BigDecimal("1681.402"))),
                ),
                investments = listOf(investments[8], investments[9]),
            ),
            InvestmentAsset(
                id = TEST_UUID,
                name = "GOOGL",
                category = "stocks",
                shares = BigDecimal("4.2"),
                costPrice = BigDecimal("1290.072"),
                marketPrice = BigDecimal("1290.072"),
                currency = "USD",
                totals = listOf(
                    InvestmentTotal("USD", BigDecimal("1290.072"), BigDecimal("1290.072"), calculatePnL(BigDecimal("1290.072"), BigDecimal("1290.072"))),
                ),
                investments = listOf(investments[0], investments[1]),
            ),
            InvestmentAsset(
                id = TEST_UUID,
                name = "GOOGL",
                category = "stocks",
                shares = BigDecimal("4.2"),
                costPrice = BigDecimal("1101.450"),
                marketPrice = BigDecimal("1101.450"),
                currency = "EUR",
                totals = listOf(
                    InvestmentTotal("EUR", BigDecimal("1101.450"), BigDecimal("1101.450"), calculatePnL(BigDecimal("1101.450"), BigDecimal("1101.450"))),
                ),
                investments = listOf(investments[12], investments[13]),
            ),
            InvestmentAsset(
                id = TEST_UUID,
                name = "Gold",
                category = "gold",
                shares = BigDecimal("2"),
                costPrice = BigDecimal("8677.10"),
                marketPrice = BigDecimal("8677.10"),
                currency = "USD",
                totals = listOf(
                    InvestmentTotal("USD", BigDecimal("8677.10"), BigDecimal("8677.10"), calculatePnL(BigDecimal("8677.10"), BigDecimal("8677.10"))),
                ),
                investments = listOf(investments[10], investments[11]),
            ),
            InvestmentAsset(
                id = TEST_UUID,
                name = "Gold",
                category = "gold",
                shares = BigDecimal("2"),
                costPrice = BigDecimal("7408.50"),
                marketPrice = BigDecimal("7408.50"),
                currency = "EUR",
                totals = listOf(
                    InvestmentTotal("EUR", BigDecimal("7408.50"), BigDecimal("7408.50"), calculatePnL(BigDecimal("7408.50"), BigDecimal("7408.50"))),
                ),
                investments = listOf(investments[16], investments[17]),
            ),
            InvestmentAsset(
                id = TEST_UUID,
                name = "NVDA",
                category = "stocks",
                shares = BigDecimal("5.0"),
                costPrice = BigDecimal("904.950"),
                marketPrice = BigDecimal("904.950"),
                currency = "USD",
                totals = listOf(
                    InvestmentTotal("USD", BigDecimal("904.950"), BigDecimal("904.950"), calculatePnL(BigDecimal("904.950"), BigDecimal("904.950"))),
                ),
                investments = listOf(investments[4], investments[5]),
            ),
            InvestmentAsset(
                id = TEST_UUID,
                name = "SNP 500",
                category = "etf",
                shares = BigDecimal("0.6"),
                costPrice = BigDecimal("4100.70"),
                marketPrice = BigDecimal("4100.70"),
                currency = "USD",
                totals = listOf(
                    InvestmentTotal("USD", BigDecimal("4100.70"), BigDecimal("4100.70"), calculatePnL(BigDecimal("4100.70"), BigDecimal("4100.70"))),
                ),
                investments = listOf(investments[6], investments[7]),
            ),
            InvestmentAsset(
                id = TEST_UUID,
                name = "SNP 500",
                category = "etf",
                shares = BigDecimal("0.6"),
                costPrice = BigDecimal("3501.18"),
                marketPrice = BigDecimal("3501.18"),
                currency = "EUR",
                totals = listOf(
                    InvestmentTotal("EUR", BigDecimal("3501.18"), BigDecimal("3501.18"), calculatePnL(BigDecimal("3501.18"), BigDecimal("3501.18"))),
                ),
                investments = listOf(investments[14], investments[15]),
            ),
        )

        val actual = Group.groupInvestmentAssets(investments, randomTestUUID)

        assertEquals(expected, actual)
    }

    @Test
    fun groupInvestmentCategories() {
        val investments = listOf(
            Investment("1", "GOOGL", BigDecimal("2.1"), BigDecimal("307.16"), BigDecimal("307.16"), "stocks", "USD", timestamp(2025, 12, 20), false, timestamp(2025, 12, 20)),
            Investment("2", "AAPL", BigDecimal("2.3"), BigDecimal("273.67"), BigDecimal("273.67"), "stocks", "USD", timestamp(2025, 12, 19), false, timestamp(2025, 12, 19)),
            Investment("3", "NVDA", BigDecimal("2.5"), BigDecimal("180.99"), BigDecimal("180.99"), "stocks", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("4", "SNP 500", BigDecimal("0.3"), BigDecimal("6834.5"), BigDecimal("6834.5"), "etf", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("5", "BRK.B", BigDecimal("1.7"), BigDecimal("494.53"), BigDecimal("494.53"), "etf", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("6", "Gold", BigDecimal("1"), BigDecimal("4338.55"), BigDecimal("4338.55"), "gold", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("7", "GOOGL", BigDecimal("2.1"), BigDecimal("262.25"), BigDecimal("262.25"), "stocks", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("8", "SNP 500", BigDecimal("0.3"), BigDecimal("5835.3"), BigDecimal("5835.3"), "etf", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("9", "Gold", BigDecimal("1"), BigDecimal("3704.25"), BigDecimal("3704.25"), "gold", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
        )

        val expected = listOf(
            InvestmentCategory(
                id = TEST_UUID,
                name = "etf",
                totals = listOf(
                    InvestmentTotal("EUR", BigDecimal("1750.59"), BigDecimal("1750.59"), calculatePnL(BigDecimal("1750.59"), BigDecimal("1750.59"))),
                    InvestmentTotal("USD", BigDecimal("2891.051"), BigDecimal("2891.051"), calculatePnL(BigDecimal("2891.051"), BigDecimal("2891.051"))),
                ),
                investments = listOf(investments[3], investments[4], investments[7]),
            ),
            InvestmentCategory(
                id = TEST_UUID,
                name = "gold",
                totals = listOf(
                    InvestmentTotal("EUR", BigDecimal("3704.25"), BigDecimal("3704.25"), calculatePnL(BigDecimal("3704.25"), BigDecimal("3704.25"))),
                    InvestmentTotal("USD", BigDecimal("4338.55"), BigDecimal("4338.55"), calculatePnL(BigDecimal("4338.55"), BigDecimal("4338.55"))),
                ),
                investments = listOf(investments[5], investments[8]),
            ),
            InvestmentCategory(
                id = TEST_UUID,
                name = "stocks",
                totals = listOf(
                    InvestmentTotal("EUR", BigDecimal("550.725"), BigDecimal("550.725"), calculatePnL(BigDecimal("550.725"), BigDecimal("550.725"))),
                    InvestmentTotal("USD", BigDecimal("1726.952"), BigDecimal("1726.952"), calculatePnL(BigDecimal("1726.952"), BigDecimal("1726.952"))),
                ),
                investments = listOf(investments[0], investments[1], investments[2], investments[6]),
            ),
        )

        val actual = Group.groupInvestmentCategories(investments, randomTestUUID)

        assertEquals(expected, actual)
    }

    @Test
    fun groupInvestmentNetWorth() {
        val investments = listOf(
            Investment("1", "GOOGL", BigDecimal("2.1"), BigDecimal("307.16"), BigDecimal("307.16"), "stocks", "USD", timestamp(2025, 12, 20), false, timestamp(2025, 12, 20)),
            Investment("2", "AAPL", BigDecimal("2.3"), BigDecimal("273.67"), BigDecimal("273.67"), "stocks", "USD", timestamp(2025, 12, 19), false, timestamp(2025, 12, 19)),
            Investment("3", "NVDA", BigDecimal("2.5"), BigDecimal("180.99"), BigDecimal("180.99"), "stocks", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("4", "SNP 500", BigDecimal("0.3"), BigDecimal("6834.5"), BigDecimal("6834.5"), "etf", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("5", "BRK.B", BigDecimal("1.7"), BigDecimal("494.53"), BigDecimal("494.53"), "etf", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("6", "Gold", BigDecimal("1"), BigDecimal("4338.55"), BigDecimal("4338.55"), "gold", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("7", "GOOGL", BigDecimal("2.1"), BigDecimal("262.25"), BigDecimal("262.25"), "stocks", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("8", "SNP 500", BigDecimal("0.3"), BigDecimal("5835.3"), BigDecimal("5835.3"), "etf", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("9", "Gold", BigDecimal("1"), BigDecimal("3704.25"), BigDecimal("3704.25"), "gold", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
        )

        val expected = InvestmentNetWorth(
            totals = listOf(
                InvestmentTotal("EUR", BigDecimal("6005.565"), BigDecimal("6005.565"), calculatePnL(BigDecimal("6005.565"), BigDecimal("6005.565"))),
                InvestmentTotal("USD", BigDecimal("8956.553"), BigDecimal("8956.553"), calculatePnL(BigDecimal("8956.553"), BigDecimal("8956.553"))),
            ),
        )
        val actual = Group.groupInvestmentNetWorth(investments)

        assertEquals(expected, actual)
    }

    @Test
    fun groupInvestmentAveragesYearly() {
        val investments = listOf(
            Investment("1", "GOOGL", BigDecimal("1"), BigDecimal("100"), BigDecimal("110"), "stocks", "USD", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25)),
            Investment("2", "AAPL", BigDecimal("1"), BigDecimal("200"), BigDecimal("220"), "stocks", "USD", timestamp(2025, 12, 9), false, timestamp(2025, 12, 9)),
            Investment("3", "SNP 500", BigDecimal("1"), BigDecimal("400"), BigDecimal("440"), "etf", "USD", timestamp(2025, 12, 17), false, timestamp(2025, 12, 17)),
            Investment("4", "GOOGL", BigDecimal("1"), BigDecimal("60"), BigDecimal("66"), "stocks", "USD", timestamp(2024, 12, 17), false, timestamp(2024, 12, 17)),
        )

        val expected = mapOf(
            // 700 over the 2 months of 2025
            "2025" to InvestmentAverage(
                totals = listOf(investmentTotal("350.00", "385.00")),
                categories = mapOf(
                    "stocks" to listOf(investmentTotal("150.00", "165.00")),
                    "etf" to listOf(investmentTotal("200.00", "220.00")),
                ),
                assets = mapOf(
                    "GOOGL" to listOf(investmentTotal("50.00", "55.00")),
                    "AAPL" to listOf(investmentTotal("100.00", "110.00")),
                    "SNP 500" to listOf(investmentTotal("200.00", "220.00")),
                ),
            ),
            // a single month has no meaningful average
            "2024" to InvestmentAverage.EMPTY,
        )

        val actual = Group.groupInvestmentAveragesYearly(investments)

        assertEquals(expected, actual)
    }

    @Test
    fun groupInvestmentAverageAllTime() {
        val investments = listOf(
            Investment("1", "GOOGL", BigDecimal("1"), BigDecimal("100"), BigDecimal("110"), "stocks", "USD", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25)),
            Investment("2", "AAPL", BigDecimal("1"), BigDecimal("200"), BigDecimal("220"), "stocks", "USD", timestamp(2025, 12, 9), false, timestamp(2025, 12, 9)),
            Investment("3", "SNP 500", BigDecimal("1"), BigDecimal("400"), BigDecimal("440"), "etf", "USD", timestamp(2025, 12, 17), false, timestamp(2025, 12, 17)),
            Investment("4", "GOOGL", BigDecimal("1"), BigDecimal("60"), BigDecimal("66"), "stocks", "USD", timestamp(2024, 12, 17), false, timestamp(2024, 12, 17)),
        )

        // 760 over 3 months
        val expected = InvestmentAverage(
            totals = listOf(investmentTotal("253.33", "278.67")),
            categories = mapOf(
                "stocks" to listOf(investmentTotal("120.00", "132.00")),
                "etf" to listOf(investmentTotal("133.33", "146.67")),
            ),
            assets = mapOf(
                "GOOGL" to listOf(investmentTotal("53.33", "58.67")),
                "AAPL" to listOf(investmentTotal("66.67", "73.33")),
                "SNP 500" to listOf(investmentTotal("133.33", "146.67")),
            ),
        )

        val actual = Group.groupInvestmentAverageAllTime(investments)

        assertEquals(expected, actual)
    }

    @Test
    fun `groupInvestmentAverageAllTime should be empty when investments span a single month`() {
        val investments = listOf(
            Investment("1", "GOOGL", BigDecimal("1"), BigDecimal("100"), BigDecimal("110"), "stocks", "USD", timestamp(2025, 12, 9), false, timestamp(2025, 12, 9)),
            Investment("2", "AAPL", BigDecimal("1"), BigDecimal("200"), BigDecimal("220"), "stocks", "USD", timestamp(2025, 12, 17), false, timestamp(2025, 12, 17)),
        )

        assertEquals(InvestmentAverage.EMPTY, Group.groupInvestmentAverageAllTime(investments))
    }

    private fun investmentTotal(costPrice: String, marketPrice: String, currency: String = "USD") =
        InvestmentTotal(
            currency = currency,
            costPrice = BigDecimal(costPrice),
            marketPrice = BigDecimal(marketPrice),
            pnl = calculatePnL(BigDecimal(costPrice), BigDecimal(marketPrice)),
        )

    @Test
    fun groupExpenseCategoriesMonthly() {
        val expenses = listOf(
            Expense("1", "Clothing", BigDecimal("120"), "EUR", "Shopping", timestamp(2025, 11, 25), false, false, timestamp(2025, 11, 25)),
            Expense("2", "Groceries", BigDecimal("75.50"), "EUR", "Food", timestamp(2025, 11, 27), false, false, timestamp(2025, 11, 27)),
            Expense("3", "Birthday gift", BigDecimal("80"), "EUR", "Gifts", timestamp(2025, 12, 9), false, false, timestamp(2025, 12, 9)),
            Expense("4", "Pet food", BigDecimal("25.5"), "EUR", "Pets", timestamp(2025, 12, 17), false, false, timestamp(2025, 12, 17)),
            Expense("5", "Gas", BigDecimal("304.27"), "EUR", "Fuel", timestamp(2024, 12, 17), false, false, timestamp(2024, 12, 17)),
            Expense("6", "Groceries", BigDecimal("150"), "EUR", "Food", timestamp(2024, 12, 17), false, false, timestamp(2024, 12, 17)),

            Expense("7", "Clothing", BigDecimal("120"), "EUR", "Shopping", timestamp(2025, 11, 25), false, false, timestamp(2025, 11, 25)),
            Expense("8", "Groceries", BigDecimal("75.50"), "EUR", "Food", timestamp(2025, 11, 27), false, false, timestamp(2025, 11, 27)),
            Expense("9", "Birthday gift", BigDecimal("80"), "EUR", "Gifts", timestamp(2025, 12, 9), false, false, timestamp(2025, 12, 9)),
            Expense("10", "Pet food", BigDecimal("25.5"), "EUR", "Pets", timestamp(2025, 12, 17), false, false, timestamp(2025, 12, 17)),
            Expense("11", "Gas", BigDecimal("304.27"), "EUR", "Fuel", timestamp(2024, 12, 17), false, false, timestamp(2024, 12, 17)),
            Expense("12", "Groceries", BigDecimal("150"), "EUR", "Food", timestamp(2024, 12, 17), false, false, timestamp(2024, 12, 17)),

            // excluded
            Expense("13", "Clothing", BigDecimal("120"), "EUR", "Shopping", timestamp(2025, 11, 25), true, false, timestamp(2025, 11, 25)),
            Expense("14", "Groceries", BigDecimal("75.50"), "EUR", "Food", timestamp(2025, 11, 27), true, false, timestamp(2025, 11, 27)),
            Expense("15", "Birthday gift", BigDecimal("80"), "EUR", "Gifts", timestamp(2025, 12, 9), true, false, timestamp(2025, 12, 9)),
            Expense("16", "Pet food", BigDecimal("25.5"), "EUR", "Pets", timestamp(2025, 12, 17), true, false, timestamp(2025, 12, 17)),
            Expense("17", "Gas", BigDecimal("304.27"), "EUR", "Fuel", timestamp(2024, 12, 17), true, false, timestamp(2024, 12, 17)),
            Expense("18", "Groceries", BigDecimal("150"), "EUR", "Food", timestamp(2024, 12, 17), true, false, timestamp(2024, 12, 17)),
        )

        val expected = listOf(
            GroupedItems(
                group = "December 2025",
                total = listOf(ExpenseTotal(BigDecimal("211.0"), "EUR")),
                items = listOf(
                    ExpenseCategory(id = TEST_UUID, "Gifts", totals = listOf(ExpenseTotal(BigDecimal("160"), "EUR")), expenses = listOf(expenses[2], expenses[8])),
                    ExpenseCategory(id = TEST_UUID, "Pets", totals = listOf(ExpenseTotal(BigDecimal("51.0"), "EUR")), expenses = listOf(expenses[3], expenses[9])),
                ),
            ),
            GroupedItems(
                group = "November 2025",
                total = listOf(ExpenseTotal(BigDecimal("391.00"), "EUR")),
                items = listOf(
                    ExpenseCategory(id = TEST_UUID, "Shopping", totals = listOf(ExpenseTotal(BigDecimal("240"), "EUR")), expenses = listOf(expenses[0], expenses[6])),
                    ExpenseCategory(id = TEST_UUID, "Food", totals = listOf(ExpenseTotal(BigDecimal("151.00"), "EUR")), expenses = listOf(expenses[1], expenses[7])),
                ),
            ),
            GroupedItems(
                group = "December 2024",
                total = listOf(ExpenseTotal(BigDecimal("908.54"), "EUR")),
                items = listOf(
                    ExpenseCategory(id = TEST_UUID, "Fuel", totals = listOf(ExpenseTotal(BigDecimal("608.54"), "EUR")), expenses = listOf(expenses[4], expenses[10])),
                    ExpenseCategory(id = TEST_UUID, "Food", totals = listOf(ExpenseTotal(BigDecimal("300"), "EUR")), expenses = listOf(expenses[5], expenses[11])),
                ),
            ),
        )

        val actual = Group.groupExpenseCategoriesMonthly(expenses, randomTestUUID)

        assertEquals(expected, actual)
    }

    @Test
    fun groupExpenseCategoriesYearly() {
        val expenses = listOf(
            Expense("1", "Clothing", BigDecimal("120"), "EUR", "Shopping", timestamp(2025, 11, 25), false, false, timestamp(2025, 11, 25)),
            Expense("2", "Groceries", BigDecimal("75.50"), "EUR", "Food", timestamp(2025, 11, 27), false, false, timestamp(2025, 11, 27)),
            Expense("3", "Birthday gift", BigDecimal("80"), "EUR", "Gifts", timestamp(2025, 12, 9), false, false, timestamp(2025, 12, 9)),
            Expense("4", "Pet food", BigDecimal("25.5"), "EUR", "Pets", timestamp(2025, 12, 17), false, false, timestamp(2025, 12, 17)),
            Expense("5", "Gas", BigDecimal("304.27"), "EUR", "Fuel", timestamp(2024, 12, 17), false, false, timestamp(2024, 12, 17)),
            Expense("6", "Groceries", BigDecimal("150"), "EUR", "Food", timestamp(2024, 12, 17), false, false, timestamp(2024, 12, 17)),

            Expense("7", "Clothing", BigDecimal("120"), "EUR", "Shopping", timestamp(2025, 11, 25), false, false, timestamp(2025, 11, 25)),
            Expense("8", "Groceries", BigDecimal("75.50"), "EUR", "Food", timestamp(2025, 11, 27), false, false, timestamp(2025, 11, 27)),
            Expense("9", "Birthday gift", BigDecimal("80"), "EUR", "Gifts", timestamp(2025, 12, 9), false, false, timestamp(2025, 12, 9)),
            Expense("10", "Pet food", BigDecimal("25.5"), "EUR", "Pets", timestamp(2025, 12, 17), false, false, timestamp(2025, 12, 17)),
            Expense("11", "Gas", BigDecimal("304.27"), "EUR", "Fuel", timestamp(2024, 12, 17), false, false, timestamp(2024, 12, 17)),
            Expense("12", "Groceries", BigDecimal("150"), "EUR", "Food", timestamp(2024, 12, 17), false, false, timestamp(2024, 12, 17)),

            Expense("13", "Clothing", BigDecimal("120"), "EUR", "Shopping", timestamp(2025, 11, 25), true, false, timestamp(2025, 11, 25)),
            Expense("14", "Groceries", BigDecimal("75.50"), "EUR", "Food", timestamp(2025, 11, 27), true, false, timestamp(2025, 11, 27)),
            Expense("15", "Birthday gift", BigDecimal("80"), "EUR", "Gifts", timestamp(2025, 12, 9), true, false, timestamp(2025, 12, 9)),
            Expense("16", "Pet food", BigDecimal("25.5"), "EUR", "Pets", timestamp(2025, 12, 17), true, false, timestamp(2025, 12, 17)),
            Expense("17", "Gas", BigDecimal("304.27"), "EUR", "Fuel", timestamp(2024, 12, 17), true, false, timestamp(2024, 12, 17)),
            Expense("18", "Groceries", BigDecimal("150"), "EUR", "Food", timestamp(2024, 12, 17), true, false, timestamp(2024, 12, 17)),
        )

        val expected = listOf(
            GroupedItems(
                group = "2025",
                total = listOf(ExpenseTotal(BigDecimal("602.00"), "EUR")),
                items = listOf(
                    ExpenseCategory(id = TEST_UUID, "Shopping", totals = listOf(ExpenseTotal(BigDecimal("240"), "EUR")), expenses = listOf(expenses[0], expenses[6])),
                    ExpenseCategory(id = TEST_UUID, "Gifts", totals = listOf(ExpenseTotal(BigDecimal("160"), "EUR")), expenses = listOf(expenses[2], expenses[8])),
                    ExpenseCategory(id = TEST_UUID, "Food", totals = listOf(ExpenseTotal(BigDecimal("151.00"), "EUR")), expenses = listOf(expenses[1], expenses[7])),
                    ExpenseCategory(id = TEST_UUID, "Pets", totals = listOf(ExpenseTotal(BigDecimal("51.0"), "EUR")), expenses = listOf(expenses[3], expenses[9])),
                ),
            ),
            GroupedItems(
                group = "2024",
                total = listOf(ExpenseTotal(BigDecimal("908.54"), "EUR")),
                items = listOf(
                    ExpenseCategory(id = TEST_UUID, "Fuel", totals = listOf(ExpenseTotal(BigDecimal("608.54"), "EUR")), expenses = listOf(expenses[4], expenses[10])),
                    ExpenseCategory(id = TEST_UUID, "Food", totals = listOf(ExpenseTotal(BigDecimal("300"), "EUR")), expenses = listOf(expenses[5], expenses[11])),
                ),
            ),
        )

        val actual = Group.groupExpenseCategoriesYearly(expenses, randomTestUUID)

        assertEquals(expected, actual)
    }

    @Test
    fun groupExpenseCategoriesAllTime() {
        val expenses = listOf(
            Expense("1", "Clothing", BigDecimal("120"), "EUR", "Shopping", timestamp(2025, 11, 25), false, false, timestamp(2025, 11, 25)),
            Expense("2", "Groceries", BigDecimal("75.50"), "EUR", "Food", timestamp(2025, 11, 27), false, false, timestamp(2025, 11, 27)),
            Expense("3", "Birthday gift", BigDecimal("80"), "EUR", "Gifts", timestamp(2025, 12, 9), false, false, timestamp(2025, 12, 9)),
            Expense("4", "Pet food", BigDecimal("25.5"), "EUR", "Pets", timestamp(2025, 12, 17), false, false, timestamp(2025, 12, 17)),
            Expense("5", "Gas", BigDecimal("304.27"), "EUR", "Fuel", timestamp(2024, 12, 17), false, false, timestamp(2024, 12, 17)),
            Expense("6", "Groceries", BigDecimal("150"), "EUR", "Food", timestamp(2024, 12, 17), false, false, timestamp(2024, 12, 17)),

            Expense("7", "Clothing", BigDecimal("120"), "EUR", "Shopping", timestamp(2025, 11, 25), false, false, timestamp(2025, 11, 25)),
            Expense("8", "Groceries", BigDecimal("75.50"), "EUR", "Food", timestamp(2025, 11, 27), false, false, timestamp(2025, 11, 27)),
            Expense("9", "Birthday gift", BigDecimal("80"), "EUR", "Gifts", timestamp(2025, 12, 9), false, false, timestamp(2025, 12, 9)),
            Expense("10", "Pet food", BigDecimal("25.5"), "EUR", "Pets", timestamp(2025, 12, 17), false, false, timestamp(2025, 12, 17)),
            Expense("11", "Gas", BigDecimal("304.27"), "EUR", "Fuel", timestamp(2024, 12, 17), false, false, timestamp(2024, 12, 17)),
            Expense("12", "Groceries", BigDecimal("150"), "EUR", "Food", timestamp(2024, 12, 17), false, false, timestamp(2024, 12, 17)),

            Expense("13", "Clothing", BigDecimal("120"), "EUR", "Shopping", timestamp(2025, 11, 25), true, false, timestamp(2025, 11, 25)),
            Expense("14", "Groceries", BigDecimal("75.50"), "EUR", "Food", timestamp(2025, 11, 27), true, false, timestamp(2025, 11, 27)),
            Expense("15", "Birthday gift", BigDecimal("80"), "EUR", "Gifts", timestamp(2025, 12, 9), true, false, timestamp(2025, 12, 9)),
            Expense("16", "Pet food", BigDecimal("25.5"), "EUR", "Pets", timestamp(2025, 12, 17), true, false, timestamp(2025, 12, 17)),
            Expense("17", "Gas", BigDecimal("304.27"), "EUR", "Fuel", timestamp(2024, 12, 17), true, false, timestamp(2024, 12, 17)),
            Expense("18", "Groceries", BigDecimal("150"), "EUR", "Food", timestamp(2024, 12, 17), true, false, timestamp(2024, 12, 17)),
        )

        val expected = listOf(
            ExpenseCategory(id = TEST_UUID, "Fuel", totals = listOf(ExpenseTotal(BigDecimal("608.54"), "EUR")), expenses = listOf(expenses[4], expenses[10])),
            ExpenseCategory(id = TEST_UUID, "Food", totals = listOf(ExpenseTotal(BigDecimal("451.00"), "EUR")), expenses = listOf(expenses[1], expenses[5], expenses[7], expenses[11])),
            ExpenseCategory(id = TEST_UUID, "Shopping", totals = listOf(ExpenseTotal(BigDecimal("240"), "EUR")), expenses = listOf(expenses[0], expenses[6])),
            ExpenseCategory(id = TEST_UUID, "Gifts", totals = listOf(ExpenseTotal(BigDecimal("160"), "EUR")), expenses = listOf(expenses[2], expenses[8])),
            ExpenseCategory(id = TEST_UUID, "Pets", totals = listOf(ExpenseTotal(BigDecimal("51.0"), "EUR")), expenses = listOf(expenses[3], expenses[9])),
        )

        val actual = Group.groupExpenseCategoriesAllTime(expenses, randomTestUUID)

        assertEquals(expected, actual)
    }

    @Test
    fun groupExpenseAveragesMonthly() {
        val expenses = listOf(
            Expense("1", "Clothing", BigDecimal("100"), "EUR", "Shopping", timestamp(2025, 11, 25), false, false, timestamp(2025, 11, 25)),
            Expense("2", "Groceries", BigDecimal("50"), "EUR", "Food", timestamp(2025, 11, 27), false, false, timestamp(2025, 11, 27)),
            Expense("3", "Groceries", BigDecimal("150"), "EUR", "Food", timestamp(2025, 12, 9), false, false, timestamp(2025, 12, 9)),
            Expense("4", "Groceries", BigDecimal("60"), "EUR", "Food", timestamp(2024, 12, 17), false, false, timestamp(2024, 12, 17)),

            // excluded from analytics, so they must not affect any average
            Expense("5", "Clothing", BigDecimal("500"), "EUR", "Shopping", timestamp(2025, 11, 25), true, false, timestamp(2025, 11, 25)),
            Expense("6", "Dinner", BigDecimal("900"), "EUR", "Food", timestamp(2025, 12, 1), true, false, timestamp(2025, 12, 1)),
        )

        // 300 over the 2 months of 2025
        val average2025 = ExpenseAverage(
            totals = listOf(ExpenseTotal(BigDecimal("150.00"), "EUR")),
            categories = mapOf(
                "Shopping" to listOf(ExpenseTotal(BigDecimal("50.00"), "EUR")),
                "Food" to listOf(ExpenseTotal(BigDecimal("100.00"), "EUR")),
            ),
        )

        val expected = mapOf(
            "November 2025" to average2025,
            "December 2025" to average2025,
            // a single month has no meaningful average
            "December 2024" to ExpenseAverage.EMPTY,
        )

        val actual = Group.groupExpenseAveragesMonthly(expenses)

        assertEquals(expected, actual)
    }

    @Test
    fun groupExpenseAveragesYearly() {
        val expenses = listOf(
            Expense("1", "Clothing", BigDecimal("100"), "EUR", "Shopping", timestamp(2025, 11, 25), false, false, timestamp(2025, 11, 25)),
            Expense("2", "Groceries", BigDecimal("50"), "EUR", "Food", timestamp(2025, 11, 27), false, false, timestamp(2025, 11, 27)),
            Expense("3", "Groceries", BigDecimal("150"), "EUR", "Food", timestamp(2025, 12, 9), false, false, timestamp(2025, 12, 9)),
            Expense("4", "Groceries", BigDecimal("60"), "EUR", "Food", timestamp(2024, 12, 17), false, false, timestamp(2024, 12, 17)),

            // excluded from analytics, so they must not affect any average
            Expense("5", "Clothing", BigDecimal("500"), "EUR", "Shopping", timestamp(2025, 11, 25), true, false, timestamp(2025, 11, 25)),
            Expense("6", "Dinner", BigDecimal("900"), "EUR", "Food", timestamp(2025, 12, 1), true, false, timestamp(2025, 12, 1)),
        )

        val expected = mapOf(
            // 300 over the 2 months of 2025
            "2025" to ExpenseAverage(
                totals = listOf(ExpenseTotal(BigDecimal("150.00"), "EUR")),
                categories = mapOf(
                    "Shopping" to listOf(ExpenseTotal(BigDecimal("50.00"), "EUR")),
                    "Food" to listOf(ExpenseTotal(BigDecimal("100.00"), "EUR")),
                ),
            ),
            // a single month has no meaningful average
            "2024" to ExpenseAverage.EMPTY,
        )

        val actual = Group.groupExpenseAveragesYearly(expenses)

        assertEquals(expected, actual)
    }

    @Test
    fun groupExpenseAverageAllTime() {
        val expenses = listOf(
            Expense("1", "Clothing", BigDecimal("100"), "EUR", "Shopping", timestamp(2025, 11, 25), false, false, timestamp(2025, 11, 25)),
            Expense("2", "Groceries", BigDecimal("50"), "EUR", "Food", timestamp(2025, 11, 27), false, false, timestamp(2025, 11, 27)),
            Expense("3", "Groceries", BigDecimal("150"), "EUR", "Food", timestamp(2025, 12, 9), false, false, timestamp(2025, 12, 9)),
            Expense("4", "Groceries", BigDecimal("60"), "EUR", "Food", timestamp(2024, 12, 17), false, false, timestamp(2024, 12, 17)),

            // excluded from analytics, so they must not affect any average
            Expense("5", "Clothing", BigDecimal("500"), "EUR", "Shopping", timestamp(2025, 11, 25), true, false, timestamp(2025, 11, 25)),
            Expense("6", "Dinner", BigDecimal("900"), "EUR", "Food", timestamp(2025, 12, 1), true, false, timestamp(2025, 12, 1)),
        )

        // 360 over 3 months
        val expected = ExpenseAverage(
            totals = listOf(ExpenseTotal(BigDecimal("120.00"), "EUR")),
            categories = mapOf(
                "Shopping" to listOf(ExpenseTotal(BigDecimal("33.33"), "EUR")),
                "Food" to listOf(ExpenseTotal(BigDecimal("86.67"), "EUR")),
            ),
        )

        val actual = Group.groupExpenseAverageAllTime(expenses)

        assertEquals(expected, actual)
    }

    @Test
    fun groupSavingCategoriesMonthly() {
        val savings = listOf(
            Saving("1", "Emergency fund", BigDecimal("500"), "EUR", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
            Saving("2", "Kids fund", BigDecimal("200"), "EUR", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
            Saving("3", "Cash", BigDecimal("250"), "EUR", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),

            Saving("4", "Emergency fund", BigDecimal("600"), "EUR", timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
            Saving("5", "Kids fund", BigDecimal("300"), "EUR", timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
            Saving("6", "Cash", BigDecimal("350"), "EUR", timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),

            Saving("7", "Emergency fund", BigDecimal("700"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
            Saving("8", "Kids fund", BigDecimal("400"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
            Saving("9", "Cash", BigDecimal("450"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),

            Saving("10", "Emergency fund", BigDecimal("800"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
            Saving("11", "Kids fund", BigDecimal("500"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
            Saving("12", "Cash", BigDecimal("450"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
        )

        val expected = listOf(
            GroupedItems(
                group = "January 2026",
                total = listOf(SavingTotal("3300".toBigDecimal(), "EUR")),
                items = listOf(
                    SavingCategory(id = TEST_UUID, "Emergency fund", totals = listOf(SavingTotal(BigDecimal("1500"), "EUR")), savings = listOf(savings[6], savings[9])),
                    SavingCategory(id = TEST_UUID, "Kids fund", totals = listOf(SavingTotal(BigDecimal("900"), "EUR")), savings = listOf(savings[7], savings[10])),
                    SavingCategory(id = TEST_UUID, "Cash", totals = listOf(SavingTotal(BigDecimal("900"), "EUR")), savings = listOf(savings[8], savings[11])),
                ),
            ),
            GroupedItems(
                group = "December 2025",
                total = listOf(SavingTotal("1250".toBigDecimal(), "EUR")),
                items = listOf(
                    SavingCategory(id = TEST_UUID, "Emergency fund", totals = listOf(SavingTotal(BigDecimal("600"), "EUR")), savings = listOf(savings[3])),
                    SavingCategory(id = TEST_UUID, "Cash", totals = listOf(SavingTotal(BigDecimal("350"), "EUR")), savings = listOf(savings[5])),
                    SavingCategory(id = TEST_UUID, "Kids fund", totals = listOf(SavingTotal(BigDecimal("300"), "EUR")), savings = listOf(savings[4])),
                ),
            ),
            GroupedItems(
                group = "November 2025",
                total = listOf(SavingTotal("950".toBigDecimal(), "EUR")),
                items = listOf(
                    SavingCategory(id = TEST_UUID, "Emergency fund", totals = listOf(SavingTotal(BigDecimal("500"), "EUR")), savings = listOf(savings[0])),
                    SavingCategory(id = TEST_UUID, "Cash", totals = listOf(SavingTotal(BigDecimal("250"), "EUR")), savings = listOf(savings[2])),
                    SavingCategory(id = TEST_UUID, "Kids fund", totals = listOf(SavingTotal(BigDecimal("200"), "EUR")), savings = listOf(savings[1])),
                ),
            ),
        )

        val actual = Group.groupSavingCategoriesMonthly(savings, randomTestUUID)

        assertEquals(expected, actual)
    }

    @Test
    fun groupSavingCategoriesYearly() {
        val savings = listOf(
            Saving("1", "Emergency fund", BigDecimal("500"), "EUR", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
            Saving("2", "Kids fund", BigDecimal("200"), "EUR", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
            Saving("3", "Cash", BigDecimal("250"), "EUR", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),

            Saving("4", "Emergency fund", BigDecimal("600"), "EUR", timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
            Saving("5", "Kids fund", BigDecimal("300"), "EUR", timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
            Saving("6", "Cash", BigDecimal("350"), "EUR", timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),

            Saving("7", "Emergency fund", BigDecimal("700"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
            Saving("8", "Kids fund", BigDecimal("400"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
            Saving("9", "Cash", BigDecimal("450"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),

            Saving("10", "Emergency fund", BigDecimal("800"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
            Saving("11", "Kids fund", BigDecimal("500"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
            Saving("12", "Cash", BigDecimal("450"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
        )

        val expected = listOf(
            GroupedItems(
                group = "2026",
                total = listOf(SavingTotal("3300".toBigDecimal(), "EUR")),
                items = listOf(
                    SavingCategory(id = TEST_UUID, "Emergency fund", totals = listOf(SavingTotal(BigDecimal("1500"), "EUR")), savings = listOf(savings[6], savings[9])),
                    SavingCategory(id = TEST_UUID, "Kids fund", totals = listOf(SavingTotal(BigDecimal("900"), "EUR")), savings = listOf(savings[7], savings[10])),
                    SavingCategory(id = TEST_UUID, "Cash", totals = listOf(SavingTotal(BigDecimal("900"), "EUR")), savings = listOf(savings[8], savings[11])),
                ),
            ),
            GroupedItems(
                group = "2025",
                total = listOf(SavingTotal("2200".toBigDecimal(), "EUR")),
                items = listOf(
                    SavingCategory(id = TEST_UUID, "Emergency fund", totals = listOf(SavingTotal(BigDecimal("1100"), "EUR")), savings = listOf(savings[0], savings[3])),
                    SavingCategory(id = TEST_UUID, "Cash", totals = listOf(SavingTotal(BigDecimal("600"), "EUR")), savings = listOf(savings[2], savings[5])),
                    SavingCategory(id = TEST_UUID, "Kids fund", totals = listOf(SavingTotal(BigDecimal("500"), "EUR")), savings = listOf(savings[1], savings[4])),
                ),
            ),
        )

        val actual = Group.groupSavingCategoriesYearly(savings, randomTestUUID)

        assertEquals(expected, actual)
    }

    @Test
    fun groupSavingCategoriesAllTime() {
        val savings = listOf(
            Saving("1", "Emergency fund", BigDecimal("500"), "EUR", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
            Saving("2", "Kids fund", BigDecimal("200"), "EUR", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
            Saving("3", "Cash", BigDecimal("250"), "EUR", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),

            Saving("4", "Emergency fund", BigDecimal("600"), "EUR", timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
            Saving("5", "Kids fund", BigDecimal("300"), "EUR", timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
            Saving("6", "Cash", BigDecimal("350"), "EUR", timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),

            Saving("7", "Emergency fund", BigDecimal("700"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
            Saving("8", "Kids fund", BigDecimal("400"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
            Saving("9", "Cash", BigDecimal("450"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),

            Saving("10", "Emergency fund", BigDecimal("800"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
            Saving("11", "Kids fund", BigDecimal("500"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
            Saving("12", "Cash", BigDecimal("450"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
        )

        val expected = listOf(
            SavingCategory(id = TEST_UUID, "Emergency fund", totals = listOf(SavingTotal(BigDecimal("2600"), "EUR")), savings = listOf(savings[0], savings[3], savings[6], savings[9])),
            SavingCategory(id = TEST_UUID, "Cash", totals = listOf(SavingTotal(BigDecimal("1500"), "EUR")), savings = listOf(savings[2], savings[5], savings[8], savings[11])),
            SavingCategory(id = TEST_UUID, "Kids fund", totals = listOf(SavingTotal(BigDecimal("1400"), "EUR")), savings = listOf(savings[1], savings[4], savings[7], savings[10])),
        )

        val actual = Group.groupSavingCategoriesAllTime(savings, randomTestUUID)

        assertEquals(expected, actual)
    }

    @Test
    fun groupBudgetCategoriesMonthly() {
        val budgets = listOf(
            Budget("1", 3000.toBigDecimal(), 5000.toBigDecimal(), 6200.toBigDecimal(), "EUR", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25)),
            Budget("2", 3200.toBigDecimal(), 5200.toBigDecimal(), 6400.toBigDecimal(), "EUR", timestamp(2025, 12, 25), false, timestamp(2025, 12, 25)),
            Budget("3", 3400.toBigDecimal(), 5400.toBigDecimal(), 6600.toBigDecimal(), "EUR", timestamp(2026, 1, 10), false, timestamp(2026, 1, 10)),
            Budget("4", 3800.toBigDecimal(), 5600.toBigDecimal(), 6800.toBigDecimal(), "EUR", timestamp(2026, 1, 25), false, timestamp(2026, 1, 25)),
        )

        val expected = listOf(
            GroupedItems(
                group = "January 2026",
                total = "7200".toBigDecimal(),
                items = listOf(
                    BudgetCategory(id = TEST_UUID, name = "4", currency = "EUR", value = BigDecimal("3800")),
                    BudgetCategory(id = TEST_UUID, name = "3", currency = "EUR", value = BigDecimal("3400")),
                ),
            ),
            GroupedItems(
                group = "December 2025",
                total = "3200".toBigDecimal(),
                items = listOf(
                    BudgetCategory(id = TEST_UUID, name = "2", currency = "EUR", value = BigDecimal("3200")),
                ),
            ),
            GroupedItems(
                group = "November 2025",
                total = "3000".toBigDecimal(),
                items = listOf(
                    BudgetCategory(id = TEST_UUID, name = "1", currency = "EUR", value = BigDecimal("3000")),
                ),
            ),
        )

        val actual = Group.groupBudgetCategoriesMonthly(budgets, randomTestUUID)

        assertEquals(expected, actual)
    }

    @Test
    fun groupBudgetCategoriesYearly() {
        val budgets = listOf(
            Budget("1", 3800.toBigDecimal(), 5600.toBigDecimal(), 6800.toBigDecimal(), "EUR", timestamp(2026, 1, 25), false, timestamp(2026, 1, 25)),
            Budget("2", 3400.toBigDecimal(), 5400.toBigDecimal(), 6600.toBigDecimal(), "EUR", timestamp(2026, 1, 10), false, timestamp(2026, 1, 10)),
            Budget("3", 3200.toBigDecimal(), 5200.toBigDecimal(), 6400.toBigDecimal(), "EUR", timestamp(2025, 12, 25), false, timestamp(2025, 12, 25)),
            Budget("4", 3000.toBigDecimal(), 5000.toBigDecimal(), 6200.toBigDecimal(), "EUR", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25)),
            Budget("5", 2800.toBigDecimal(), 4800.toBigDecimal(), 6000.toBigDecimal(), "EUR", timestamp(2025, 10, 25), false, timestamp(2025, 10, 25)),
            Budget("6", 2600.toBigDecimal(), 4600.toBigDecimal(), 5800.toBigDecimal(), "EUR", timestamp(2025, 9, 25), false, timestamp(2025, 9, 25)),
        )

        val expected = listOf(
            GroupedItems(
                group = "2026",
                total = "7200".toBigDecimal(),
                items = listOf(
                    BudgetCategory(id = TEST_UUID, name = "1", currency = "EUR", value = BigDecimal("3800")),
                    BudgetCategory(id = TEST_UUID, name = "2", currency = "EUR", value = BigDecimal("3400")),
                ),
            ),
            GroupedItems(
                group = "2025",
                total = "11600".toBigDecimal(),
                items = listOf(
                    BudgetCategory(id = TEST_UUID, name = "3", currency = "EUR", value = BigDecimal("3200")),
                    BudgetCategory(id = TEST_UUID, name = "4", currency = "EUR", value = BigDecimal("3000")),
                    BudgetCategory(id = TEST_UUID, name = "5", currency = "EUR", value = BigDecimal("2800")),
                    BudgetCategory(id = TEST_UUID, name = "6", currency = "EUR", value = BigDecimal("2600")),
                ),
            ),
        )

        val actual = Group.groupBudgetCategoriesYearly(budgets, randomTestUUID)

        assertEquals(expected, actual)
    }

    @Test
    fun groupBudgetCategoriesAllTime() {
        val budgets = listOf(
            Budget("1", 3800.toBigDecimal(), 5600.toBigDecimal(), 6800.toBigDecimal(), "EUR", timestamp(2026, 1, 25), false, timestamp(2026, 1, 25)),
            Budget("2", 3400.toBigDecimal(), 5400.toBigDecimal(), 6600.toBigDecimal(), "EUR", timestamp(2026, 1, 10), false, timestamp(2026, 1, 10)),
            Budget("3", 3200.toBigDecimal(), 5200.toBigDecimal(), 6400.toBigDecimal(), "EUR", timestamp(2025, 12, 25), false, timestamp(2025, 12, 25)),
            Budget("4", 3000.toBigDecimal(), 5000.toBigDecimal(), 6200.toBigDecimal(), "EUR", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25)),
            Budget("5", 2800.toBigDecimal(), 4800.toBigDecimal(), 6000.toBigDecimal(), "EUR", timestamp(2025, 10, 25), false, timestamp(2025, 10, 25)),
            Budget("6", 2600.toBigDecimal(), 4600.toBigDecimal(), 5800.toBigDecimal(), "EUR", timestamp(2025, 9, 25), false, timestamp(2025, 9, 25)),
        )

        val expected = BudgetCategory(
            id = TEST_UUID,
            name = TEST_UUID,
            currency = "EUR",
            value = "18800".toBigDecimal(),
        )

        val actual = Group.groupBudgetCategoriesAllTime(budgets, randomTestUUID)

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

        private const val TEST_UUID = "1"

        private val randomTestUUID = { TEST_UUID }

    }

}
