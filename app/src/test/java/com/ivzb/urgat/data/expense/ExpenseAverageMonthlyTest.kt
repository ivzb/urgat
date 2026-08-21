package com.ivzb.urgat.data.expense

import com.ivzb.urgat.util.timestamp
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class ExpenseAverageMonthlyTest {

    @Test
    fun `months should count distinct months`() {
        val expenses = listOf(
            expense(id = "1", amount = "100", date = timestamp(2025, 1, 5)),
            expense(id = "2", amount = "100", date = timestamp(2025, 1, 25)),
            expense(id = "3", amount = "100", date = timestamp(2025, 2, 3)),
            expense(id = "4", amount = "100", date = timestamp(2026, 1, 3)),
        )

        assertEquals(3, Expense.months(expenses))
    }

    @Test
    fun `months should exclude expenses excluded from analytics`() {
        val expenses = listOf(
            expense(id = "1", amount = "100", date = timestamp(2025, 1, 5)),
            expense(id = "2", amount = "100", date = timestamp(2025, 2, 3), excludeFromAnalytics = true),
        )

        assertEquals(1, Expense.months(expenses))
    }

    @Test
    fun `months should be zero when there are no expenses`() {
        assertEquals(0, Expense.months(listOf()))
    }

    @Test
    fun `averageMonthly should divide totals by the months the expenses span over`() {
        val expenses = listOf(
            expense(id = "1", amount = "100", date = timestamp(2025, 1, 5)),
            expense(id = "2", amount = "200", date = timestamp(2025, 2, 3)),
            expense(id = "3", amount = "300", date = timestamp(2025, 3, 3)),
        )

        assertEquals(
            listOf(ExpenseTotal(BigDecimal("200.00"), "EUR")),
            Expense.averageMonthly(expenses),
        )
    }

    @Test
    fun `averageMonthly should divide totals by the given months`() {
        val expenses = listOf(
            expense(id = "1", amount = "100", date = timestamp(2025, 1, 5)),
            expense(id = "2", amount = "200", date = timestamp(2025, 2, 3)),
        )

        assertEquals(
            listOf(ExpenseTotal(BigDecimal("25.00"), "EUR")),
            Expense.averageMonthly(expenses, months = 12),
        )
    }

    @Test
    fun `averageMonthly should be calculated per currency`() {
        val expenses = listOf(
            expense(id = "1", amount = "100", currency = "EUR", date = timestamp(2025, 1, 5)),
            expense(id = "2", amount = "300", currency = "BGN", date = timestamp(2025, 2, 3)),
        )

        assertEquals(
            listOf(
                ExpenseTotal(BigDecimal("150.00"), "BGN"),
                ExpenseTotal(BigDecimal("50.00"), "EUR"),
            ),
            Expense.averageMonthly(expenses),
        )
    }

    @Test
    fun `averageMonthly should be empty when there are no expenses`() {
        assertEquals(listOf(), Expense.averageMonthly(listOf()))
    }

    @Test
    fun `averageMonthly should be empty when months are not positive`() {
        val expenses = listOf(
            expense(id = "1", amount = "100", date = timestamp(2025, 1, 5)),
        )

        assertEquals(listOf(), Expense.averageMonthly(expenses, months = 0))
    }

    private fun expense(
        id: String,
        amount: String,
        date: Long,
        currency: String = "EUR",
        category: String = "Food",
        excludeFromAnalytics: Boolean = false,
    ) = Expense(
        id = id,
        name = "expense $id",
        amount = BigDecimal(amount),
        currency = currency,
        category = category,
        date = date,
        excludeFromAnalytics = excludeFromAnalytics,
        isDeleted = false,
        createdAt = date,
    )
}


