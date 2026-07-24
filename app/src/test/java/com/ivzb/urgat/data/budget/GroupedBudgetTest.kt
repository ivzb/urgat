package com.ivzb.urgat.data.budget

import com.ivzb.urgat.domain.budget.GroupedBudgetCategories
import org.junit.Test
import kotlin.test.assertEquals

class GroupedBudgetTest {

    @Test
    fun testGroupedBudget_positive() =
        testGroupedBudget(
            income = 6600,
            expenses = 2200,
            investments = 3300,
            trades = 300,
            savings = 500,

            expectedRemaining = 300,
            expectedTotal = 6600,
        )

    @Test
    fun testGroupedBudget_zero() =
        testGroupedBudget(
            income = 6600,
            expenses = 2200,
            investments = 3300,
            trades = 600,
            savings = 500,

            expectedRemaining = 0,
            expectedTotal = 6600,
        )

    @Test
    fun testGroupedBudget_negative() =
        testGroupedBudget(
            income = 6600,
            expenses = 2200,
            investments = 3300,
            trades = 500,
            savings = 2100,

            expectedRemaining = -1500,
            expectedTotal = 8100,
        )

    private fun testGroupedBudget(
        income: Int,
        expenses: Int,
        investments: Int,
        trades: Int,
        savings: Int,

        expectedRemaining: Int,
        expectedTotal: Int,
    ) {
        val groupedBudget = GroupedBudget(
            category = GroupedBudgetCategories(
                group = "test",
                total = income.toBigDecimal(),
                items = listOf(),
            ),
            currency = "EUR",
            totalIncome = income.toBigDecimal(),
            totalExpenses = expenses.toBigDecimal(),
            totalInvestments = investments.toBigDecimal(),
            totalTrades = trades.toBigDecimal(),
            totalSavings = savings.toBigDecimal(),
        )

        val expectedRemaining = expectedRemaining.toBigDecimal()
        val actualRemaining = groupedBudget.remaining

        val expectedTotal = expectedTotal.toBigDecimal()
        val actualTotal = groupedBudget.total

        assertEquals(expectedRemaining, actualRemaining)
        assertEquals(expectedTotal, actualTotal)
    }

}