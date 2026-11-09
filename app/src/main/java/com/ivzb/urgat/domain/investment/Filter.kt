package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.ui.investments.INVESTMENTS_FILTER_TYPE_ALL
import com.ivzb.urgat.util.isZero

fun filterInvestments(items: List<Investment>, filterType: String): List<Investment> {
    if (filterType == INVESTMENTS_FILTER_TYPE_ALL) {
        return items
    }

    return items.filter { it.category == filterType }
}

fun excludeClosedPositions(items: List<Investment>): List<Investment> {
    val closedPositions = items
        .groupBy { Triple(it.name, it.category, it.currency) }
        .values
        .filter { position -> position.sumOf { it.shares }.isZero() }
        .flatten()
        .toSet()

    return items - closedPositions
}
