package com.ivzb.urgat.domain.common

import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.saving.SavingCategory
import com.ivzb.urgat.data.saving.SavingTotal
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.util.timestamp
import java.math.BigDecimal
import kotlin.test.assertEquals
import org.junit.Test

class GroupSavingsTest {

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

    companion object {

        private const val TEST_UUID = "1"

        private val randomTestUUID = { TEST_UUID }

    }

}
