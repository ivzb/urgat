package com.ivzb.urgat.domain.common

import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.budget.BudgetCategory
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.util.timestamp
import java.math.BigDecimal
import kotlin.test.assertEquals
import org.junit.Test

class GroupBudgetsTest {

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

        private const val TEST_UUID = "1"

        private val randomTestUUID = { TEST_UUID }

    }

}
