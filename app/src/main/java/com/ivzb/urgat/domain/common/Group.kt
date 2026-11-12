package com.ivzb.urgat.domain.common

import com.ivzb.urgat.data.expense.Expense.Companion.format
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.util.toLocalDate
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.collections.component1
import kotlin.collections.component2

object Group {

    internal val YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy")
    private val YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy")

    fun <Item, Total> byDateMonthly(
        items: List<Item>,
        date: (Item) -> Long,
        total: (List<Item>) -> Total,
    ): List<GroupedItems<String, Total, Item>> =
        items
            .groupBy {
                val date = date(it).toLocalDate()
                YearMonth.from(date)
            }
            .map { (yearMonth, items) ->
                GroupedItems(
                    group = yearMonth,
                    total = total(items),
                    items = items,
                )
            }
            .sortedByDescending { it.group }
            .map {
                GroupedItems(
                    group = it.group.format(YEAR_MONTH_FORMATTER),
                    total = it.total,
                    items = it.items,
                )
            }

    fun <Item, Total> byDateYearly(
        items: List<Item>,
        date: (Item) -> Long,
        total: (List<Item>) -> Total
    ): List<GroupedItems<String, Total, Item>> =
        items
            .groupBy {
                val date = date(it).toLocalDate()
                Year.from(date)
            }
            .map { (year, items) ->
                GroupedItems(
                    group = year,
                    total = total(items),
                    items = items,
                )
            }
            .sortedByDescending { it.group }
            .map {
                GroupedItems(
                    group = it.group.format(YEAR_FORMATTER),
                    total = it.total,
                    items = it.items
                )
            }

    fun <Item, Total> byDateAllTime(
        items: List<Item>,
        total: (List<Item>) -> Total
    ): GroupedItems<String, Total, Item> =
        items
            .let {
                GroupedItems(
                    group = "",
                    total = total(it),
                    items = it,
                )
            }

}
