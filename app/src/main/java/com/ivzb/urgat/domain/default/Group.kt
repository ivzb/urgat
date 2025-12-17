package com.ivzb.urgat.domain.default

import com.ivzb.urgat.ui.GroupedItems
import com.ivzb.urgat.util.toLocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.collections.component1
import kotlin.collections.component2

object Group {

    private val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")

    fun <T> byDate(items: List<T>, date: (T) -> Long): List<GroupedItems<String, T>> {
        return items
            .groupBy {
                val date = date(it).toLocalDate()
                YearMonth.from(date)
            }
            .map { (yearMonth, investments) ->
                GroupedItems(
                    group = yearMonth,
                    items = investments.sortedByDescending { date(it) }
                )
            }
            .sortedByDescending { it.group }
            .map {
                GroupedItems(
                    group = it.group.format(formatter),
                    items = it.items
                )
            }
    }

}