package com.ivzb.urgat.domain.common

import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentAsset
import com.ivzb.urgat.data.investment.InvestmentNetWorth
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

    fun groupInvestmentAssets(investments: List<Investment>): List<InvestmentAsset> {
        return investments
            .groupBy { Pair(it.currency, it.category) }
            .map { (key, investments) ->
                val (currency, category) = key

                InvestmentAsset(
                    category = category,
                    amount = investments.sumOf { it.amount * it.cost },
                    currency = currency,
                )
            }
            .sortedBy { it.category }
    }

    fun groupInvestmentNetWorth(investments: List<Investment>): List<InvestmentNetWorth> =
        investments
            .groupBy { it.currency }
            .map { (currency, investments) ->
                InvestmentNetWorth(
                    amount = investments.sumOf { it.amount * it.cost },
                    currency = currency,
                )
            }
            .sortedBy { it.currency }

}