package com.ivzb.urgat.domain.common

import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.util.timestamp
import kotlin.test.assertEquals
import org.junit.Test

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
