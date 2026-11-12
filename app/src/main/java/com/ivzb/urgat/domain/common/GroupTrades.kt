package com.ivzb.urgat.domain.common

import com.ivzb.urgat.data.trade.Trade
import com.ivzb.urgat.data.trade.TradeCategory
import com.ivzb.urgat.domain.common.Group.byDateMonthly
import com.ivzb.urgat.domain.common.Group.byDateYearly
import com.ivzb.urgat.domain.trades.GroupedTradeCategories
import com.ivzb.urgat.ui.components.GroupedItems
import kotlin.collections.component1
import kotlin.collections.component2

object GroupTrades {

    fun groupTradeCategoriesMonthly(trades: List<Trade>, random: () -> String): List<GroupedTradeCategories> =
        byDateMonthly(trades, Trade::date, Trade::total)
            .map { (group, total, items) ->
                GroupedItems(
                    group = group,
                    total = total,
                    items = groupTrades(items, random),
                )
            }

    fun groupTradeCategoriesYearly(trades: List<Trade>, random: () -> String): List<GroupedTradeCategories> =
        byDateYearly(trades, Trade::date, Trade::total)
            .map { (group, total, items) ->
                GroupedItems(
                    group = group,
                    total = total,
                    items = groupTrades(items, random),
                )
            }

    fun groupTradeCategoriesAllTime(trades: List<Trade>, random: () -> String): List<TradeCategory> =
        groupTrades(trades, random)

    private fun groupTrades(trades: List<Trade>, random: () -> String): List<TradeCategory> =
        trades
            .groupBy { it.name }
            .map { (category, trades) ->
                TradeCategory(
                    id = random(),
                    name = category,
                    totals = Trade.total(trades),

                    trades = trades,
                )
            }
            .sortedByDescending { it.totals.sumOf { it.price } }

}
