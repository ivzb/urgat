package com.ivzb.urgat.domain.common

import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpenseAverage
import com.ivzb.urgat.data.expense.ExpenseCategory
import com.ivzb.urgat.data.expense.ExpenseTotal
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.util.timestamp
import java.math.BigDecimal
import kotlin.test.assertEquals
import org.junit.Test

class GroupExpensesTest {

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

    companion object {

        private const val TEST_UUID = "1"

        private val randomTestUUID = { TEST_UUID }

    }

}
