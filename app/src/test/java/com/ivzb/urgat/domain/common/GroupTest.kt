package com.ivzb.urgat.domain.common

import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpenseCategory
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentAsset
import com.ivzb.urgat.data.investment.InvestmentCategory
import com.ivzb.urgat.data.investment.InvestmentNetWorth
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.util.timestamp
import java.math.BigDecimal
import kotlin.test.Test
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
                    GroupItemTest(name = "item4", currency = "USD", date = 1764885600000),
                    GroupItemTest(name = "item3", currency = "EUR", date = 1763589600000),
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
            InvestmentAsset(id = TEST_UUID, name="AAPL", category = "stocks", shares = BigDecimal("4.6"), marketValue = BigDecimal("1258.882"), currency="USD", investments = listOf(investments[2], investments[3]), total = "1,258.88 USD",),
            InvestmentAsset(id = TEST_UUID, name="BRK.B", category = "etf", shares = BigDecimal("3.4"), marketValue = BigDecimal("1681.402"), currency="USD", investments = listOf(investments[8], investments[9]), total = "1,681.4 USD",),
            InvestmentAsset(id = TEST_UUID, name="GOOGL", category = "stocks", shares = BigDecimal("4.2"), marketValue = BigDecimal("1290.072"), currency="USD", investments = listOf(investments[0], investments[1]), total = "1,290.07 USD",),
            InvestmentAsset(id = TEST_UUID, name="GOOGL", category = "stocks", shares = BigDecimal("4.2"), marketValue = BigDecimal("1101.450"), currency="EUR", investments = listOf(investments[12], investments[13]), total = "1,101.45 EUR",),
            InvestmentAsset(id = TEST_UUID, name="Gold", category = "gold", shares = BigDecimal("2"), marketValue = BigDecimal("8677.10"), currency="USD", investments = listOf(investments[10], investments[11]), total = "8,677.1 USD",),
            InvestmentAsset(id = TEST_UUID, name="Gold", category = "gold", shares = BigDecimal("2"), marketValue = BigDecimal("7408.50"), currency="EUR", investments = listOf(investments[16], investments[17]), total = "7,408.5 EUR",),
            InvestmentAsset(id = TEST_UUID, name="NVDA", category = "stocks", shares = BigDecimal("5.0"), marketValue = BigDecimal("904.950"), currency="USD", investments = listOf(investments[4], investments[5]), total = "904.95 USD",),
            InvestmentAsset(id = TEST_UUID, name="SNP 500", category = "etf", shares = BigDecimal("0.6"), marketValue = BigDecimal("4100.70"), currency="USD", investments = listOf(investments[6], investments[7]), total = "4,100.7 USD",),
            InvestmentAsset(id = TEST_UUID, name="SNP 500", category = "etf", shares = BigDecimal("0.6"), marketValue = BigDecimal("3501.18"), currency="EUR", investments = listOf(investments[14], investments[15]), total = "3,501.18 EUR",),
        )

        val actual = Group.groupInvestmentAssets(investments, randomTestUUID)

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
            InvestmentCategory(id = TEST_UUID, name = "etf", total = "1,750.59 EUR | 2,891.05 USD", investments = listOf(investments[3], investments[4], investments[7])),
            InvestmentCategory(id = TEST_UUID, name = "gold", total = "3,704.25 EUR | 4,338.55 USD", investments = listOf(investments[5], investments[8])),
            InvestmentCategory(id = TEST_UUID, name = "stocks", total = "550.72 EUR | 1,726.95 USD", investments = listOf(investments[0], investments[1], investments[2], investments[6])),
        )

        val actual = Group.groupInvestmentCategories(investments, randomTestUUID)

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

        val expected = InvestmentNetWorth(total = "6,005.56 EUR | 8,956.55 USD")
        val actual = Group.groupInvestmentNetWorth(investments)

        assertEquals(expected, actual)
    }

    @Test
    fun groupExpenseCategoriesMonthly() {
        val expenses = listOf(
            Expense("1", "Clothing", BigDecimal("120"), "Shopping", timestamp(2025, 11, 25), false, false, timestamp(2025, 11, 25)),
            Expense("2", "Groceries", BigDecimal("75.50"), "Food", timestamp(2025, 11, 27), false, false, timestamp(2025, 11, 27)),
            Expense("3", "Birthday gift", BigDecimal("80"), "Gifts", timestamp(2025, 12, 9), false, false, timestamp(2025, 12, 9)),
            Expense("4", "Pet food", BigDecimal("25.5"), "Pets", timestamp(2025, 12, 17), false, false, timestamp(2025, 12, 17)),
            Expense("5", "Gas", BigDecimal("304.27"), "Fuel", timestamp(2024, 12, 17), false, false, timestamp(2024, 12, 17)),
            Expense("6", "Groceries", BigDecimal("150"), "Food", timestamp(2024, 12, 17), false, false, timestamp(2024, 12, 17)),

            Expense("7", "Clothing", BigDecimal("120"), "Shopping", timestamp(2025, 11, 25), false, false, timestamp(2025, 11, 25)),
            Expense("8", "Groceries", BigDecimal("75.50"), "Food", timestamp(2025, 11, 27), false, false, timestamp(2025, 11, 27)),
            Expense("9", "Birthday gift", BigDecimal("80"), "Gifts", timestamp(2025, 12, 9), false, false, timestamp(2025, 12, 9)),
            Expense("10", "Pet food", BigDecimal("25.5"), "Pets", timestamp(2025, 12, 17), false, false, timestamp(2025, 12, 17)),
            Expense("11", "Gas", BigDecimal("304.27"), "Fuel", timestamp(2024, 12, 17), false, false, timestamp(2024, 12, 17)),
            Expense("12", "Groceries", BigDecimal("150"), "Food", timestamp(2024, 12, 17), false, false, timestamp(2024, 12, 17)),

            // excluded
            Expense("13", "Clothing", BigDecimal("120"), "Shopping", timestamp(2025, 11, 25), true, false, timestamp(2025, 11, 25)),
            Expense("14", "Groceries", BigDecimal("75.50"), "Food", timestamp(2025, 11, 27), true, false, timestamp(2025, 11, 27)),
            Expense("15", "Birthday gift", BigDecimal("80"), "Gifts", timestamp(2025, 12, 9), true, false, timestamp(2025, 12, 9)),
            Expense("16", "Pet food", BigDecimal("25.5"), "Pets", timestamp(2025, 12, 17), true, false, timestamp(2025, 12, 17)),
            Expense("17", "Gas", BigDecimal("304.27"), "Fuel", timestamp(2024, 12, 17), true, false, timestamp(2024, 12, 17)),
            Expense("18", "Groceries", BigDecimal("150"), "Food", timestamp(2024, 12, 17), true, false, timestamp(2024, 12, 17)),
        )

        val expected = listOf(
            GroupedItems(
                group = "December 2025",
                total = "211.0".toBigDecimal(),
                items = listOf(
                    ExpenseCategory(id = TEST_UUID, "Gifts", BigDecimal("160"), expenses = listOf(expenses[2], expenses[8])),
                    ExpenseCategory(id = TEST_UUID, "Pets", BigDecimal("51.0"), expenses = listOf(expenses[3], expenses[9])),
                ),
            ),
            GroupedItems(
                group = "November 2025",
                total = "391.00".toBigDecimal(),
                items = listOf(
                    ExpenseCategory(id = TEST_UUID, "Shopping", BigDecimal("240"), expenses = listOf(expenses[0], expenses[6])),
                    ExpenseCategory(id = TEST_UUID, "Food", BigDecimal("151.00"), expenses = listOf(expenses[1], expenses[7])),
                ),
            ),
            GroupedItems(
                group = "December 2024",
                total = "908.54".toBigDecimal(),
                items = listOf(
                    ExpenseCategory(id = TEST_UUID, "Fuel", BigDecimal("608.54"), expenses = listOf(expenses[4], expenses[10])),
                    ExpenseCategory(id = TEST_UUID, "Food", BigDecimal("300"), expenses = listOf(expenses[5], expenses[11])),
                ),
            ),
        )

        val actual = Group.groupExpenseCategoriesMonthly(expenses, randomTestUUID)

        assertEquals(expected, actual)
    }

    @Test
    fun groupExpenseCategoriesYearly() {
        val expenses = listOf(
            Expense("1", "Clothing", BigDecimal("120"), "Shopping", timestamp(2025, 11, 25), false, false, timestamp(2025, 11, 25)),
            Expense("2", "Groceries", BigDecimal("75.50"), "Food", timestamp(2025, 11, 27), false, false, timestamp(2025, 11, 27)),
            Expense("3", "Birthday gift", BigDecimal("80"), "Gifts", timestamp(2025, 12, 9), false, false, timestamp(2025, 12, 9)),
            Expense("4", "Pet food", BigDecimal("25.5"), "Pets", timestamp(2025, 12, 17), false, false, timestamp(2025, 12, 17)),
            Expense("5", "Gas", BigDecimal("304.27"), "Fuel", timestamp(2024, 12, 17), false, false, timestamp(2024, 12, 17)),
            Expense("6", "Groceries", BigDecimal("150"), "Food", timestamp(2024, 12, 17), false, false, timestamp(2024, 12, 17)),

            Expense("7", "Clothing", BigDecimal("120"), "Shopping", timestamp(2025, 11, 25), false, false, timestamp(2025, 11, 25)),
            Expense("8", "Groceries", BigDecimal("75.50"), "Food", timestamp(2025, 11, 27), false, false, timestamp(2025, 11, 27)),
            Expense("9", "Birthday gift", BigDecimal("80"), "Gifts", timestamp(2025, 12, 9), false, false, timestamp(2025, 12, 9)),
            Expense("10", "Pet food", BigDecimal("25.5"), "Pets", timestamp(2025, 12, 17), false, false, timestamp(2025, 12, 17)),
            Expense("11", "Gas", BigDecimal("304.27"), "Fuel", timestamp(2024, 12, 17), false, false, timestamp(2024, 12, 17)),
            Expense("12", "Groceries", BigDecimal("150"), "Food", timestamp(2024, 12, 17), false, false, timestamp(2024, 12, 17)),

            Expense("13", "Clothing", BigDecimal("120"), "Shopping", timestamp(2025, 11, 25), true, false, timestamp(2025, 11, 25)),
            Expense("14", "Groceries", BigDecimal("75.50"), "Food", timestamp(2025, 11, 27), true, false, timestamp(2025, 11, 27)),
            Expense("15", "Birthday gift", BigDecimal("80"), "Gifts", timestamp(2025, 12, 9), true, false, timestamp(2025, 12, 9)),
            Expense("16", "Pet food", BigDecimal("25.5"), "Pets", timestamp(2025, 12, 17), true, false, timestamp(2025, 12, 17)),
            Expense("17", "Gas", BigDecimal("304.27"), "Fuel", timestamp(2024, 12, 17), true, false, timestamp(2024, 12, 17)),
            Expense("18", "Groceries", BigDecimal("150"), "Food", timestamp(2024, 12, 17), true, false, timestamp(2024, 12, 17)),
        )

        val expected = listOf(
            GroupedItems(
                group = "2025",
                total = "602.00".toBigDecimal(),
                items = listOf(
                    ExpenseCategory(id = TEST_UUID, "Shopping", BigDecimal("240"), listOf(expenses[0], expenses[6])),
                    ExpenseCategory(id = TEST_UUID, "Gifts", BigDecimal("160"), listOf(expenses[2], expenses[8])),
                    ExpenseCategory(id = TEST_UUID, "Food", BigDecimal("151.00"), listOf(expenses[1], expenses[7])),
                    ExpenseCategory(id = TEST_UUID, "Pets", BigDecimal("51.0"), listOf(expenses[3], expenses[9])),
                ),
            ),
            GroupedItems(
                group = "2024",
                total = "908.54".toBigDecimal(),
                items = listOf(
                    ExpenseCategory(id = TEST_UUID, "Fuel", BigDecimal("608.54"), listOf(expenses[4], expenses[10])),
                    ExpenseCategory(id = TEST_UUID, "Food", BigDecimal("300"), listOf(expenses[5], expenses[11])),
                ),
            ),
        )

        val actual = Group.groupExpenseCategoriesYearly(expenses, randomTestUUID)

        assertEquals(expected, actual)
    }

    @Test
    fun groupExpenseCategoriesAllTime() {
        val expenses = listOf(
            Expense("1", "Clothing", BigDecimal("120"), "Shopping", timestamp(2025, 11, 25), false, false, timestamp(2025, 11, 25)),
            Expense("2", "Groceries", BigDecimal("75.50"), "Food", timestamp(2025, 11, 27), false, false, timestamp(2025, 11, 27)),
            Expense("3", "Birthday gift", BigDecimal("80"), "Gifts", timestamp(2025, 12, 9), false, false, timestamp(2025, 12, 9)),
            Expense("4", "Pet food", BigDecimal("25.5"), "Pets", timestamp(2025, 12, 17), false, false, timestamp(2025, 12, 17)),
            Expense("5", "Gas", BigDecimal("304.27"), "Fuel", timestamp(2024, 12, 17), false, false, timestamp(2024, 12, 17)),
            Expense("6", "Groceries", BigDecimal("150"), "Food", timestamp(2024, 12, 17), false, false, timestamp(2024, 12, 17)),

            Expense("7", "Clothing", BigDecimal("120"), "Shopping", timestamp(2025, 11, 25), false, false, timestamp(2025, 11, 25)),
            Expense("8", "Groceries", BigDecimal("75.50"), "Food", timestamp(2025, 11, 27), false, false, timestamp(2025, 11, 27)),
            Expense("9", "Birthday gift", BigDecimal("80"), "Gifts", timestamp(2025, 12, 9), false, false, timestamp(2025, 12, 9)),
            Expense("10", "Pet food", BigDecimal("25.5"), "Pets", timestamp(2025, 12, 17), false, false, timestamp(2025, 12, 17)),
            Expense("11", "Gas", BigDecimal("304.27"), "Fuel", timestamp(2024, 12, 17), false, false, timestamp(2024, 12, 17)),
            Expense("12", "Groceries", BigDecimal("150"), "Food", timestamp(2024, 12, 17), false, false, timestamp(2024, 12, 17)),

            Expense("13", "Clothing", BigDecimal("120"), "Shopping", timestamp(2025, 11, 25), true, false, timestamp(2025, 11, 25)),
            Expense("14", "Groceries", BigDecimal("75.50"), "Food", timestamp(2025, 11, 27), true, false, timestamp(2025, 11, 27)),
            Expense("15", "Birthday gift", BigDecimal("80"), "Gifts", timestamp(2025, 12, 9), true, false, timestamp(2025, 12, 9)),
            Expense("16", "Pet food", BigDecimal("25.5"), "Pets", timestamp(2025, 12, 17), true, false, timestamp(2025, 12, 17)),
            Expense("17", "Gas", BigDecimal("304.27"), "Fuel", timestamp(2024, 12, 17), true, false, timestamp(2024, 12, 17)),
            Expense("18", "Groceries", BigDecimal("150"), "Food", timestamp(2024, 12, 17), true, false, timestamp(2024, 12, 17)),
        )

        val expected = listOf(
            ExpenseCategory(id = TEST_UUID, "Fuel", BigDecimal("608.54"), listOf(expenses[4], expenses[10])),
            ExpenseCategory(id = TEST_UUID, "Food", BigDecimal("451.00"), listOf(expenses[1], expenses[5], expenses[7], expenses[11])),
            ExpenseCategory(id = TEST_UUID, "Shopping", BigDecimal("240"), listOf(expenses[0], expenses[6])),
            ExpenseCategory(id = TEST_UUID, "Gifts", BigDecimal("160"), listOf(expenses[2], expenses[8])),
            ExpenseCategory(id = TEST_UUID, "Pets", BigDecimal("51.0"), listOf(expenses[3], expenses[9])),
        )

        val actual = Group.groupExpenseCategoriesAllTime(expenses, randomTestUUID)

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