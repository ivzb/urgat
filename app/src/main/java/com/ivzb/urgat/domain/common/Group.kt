package com.ivzb.urgat.domain.common

import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentAsset
import com.ivzb.urgat.data.investment.InvestmentCategory
import com.ivzb.urgat.data.investment.InvestmentNetWorth
import com.ivzb.urgat.ui.GroupedItems
import com.ivzb.urgat.util.toLocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.collections.component1
import kotlin.collections.component2

object Group {

    private val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")

    fun <Item, Total> byDate(items: List<Item>, date: (Item) -> Long, total: (List<Item>) -> Total): List<GroupedItems<String, Total, Item>> {
        return items
            .groupBy {
                val date = date(it).toLocalDate()
                YearMonth.from(date)
            }
            .map { (yearMonth, items) ->
                GroupedItems(
                    group = yearMonth,
                    total = total(items),
                    items = items.sortedByDescending { date(it) }
                )
            }
            .sortedByDescending { it.group }
            .map {
                GroupedItems(
                    group = it.group.format(formatter),
                    total = it.total,
                    items = it.items
                )
            }
    }

    fun groupInvestmentAssets(investments: List<Investment>): List<InvestmentAsset> {
        return investments
            .groupBy { Triple(it.name, it.category, it.currency) }
            .map { (key, investments) ->
                val (name, category, currency) = key

                InvestmentAsset(
                    name = name,
                    category = category,
                    shares = investments.sumOf { it.shares },
                    marketValue = investments.sumOf { it.marketValue },
                    currency = currency,
                )
            }
            .sortedBy { it.name }
    }

    fun groupInvestmentCategories(investments: List<Investment>): List<InvestmentCategory> {
        return investments
            .groupBy { Pair(it.currency, it.category) }
            .map { (key, investments) ->
                val (currency, category) = key

                InvestmentCategory(
                    name = category,
                    marketValue = investments.sumOf { it.marketValue },
                    currency = currency,
                )
            }
            .sortedBy { it.name }
    }

    fun groupInvestmentNetWorth(investments: List<Investment>): List<InvestmentNetWorth> =
        investments
            .groupBy { it.currency }
            .map { (currency, investments) ->
                InvestmentNetWorth(
                    marketValue = investments.sumOf { it.marketValue },
                    currency = currency,
                )
            }
            .sortedBy { it.currency }

}