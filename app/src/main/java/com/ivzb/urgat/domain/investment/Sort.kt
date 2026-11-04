package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.ui.investments.InvestmentsSortType

fun sortInvestments(items: List<Investment>, sortType: InvestmentsSortType): List<Investment> = when (sortType) {
    InvestmentsSortType.BY_DATE -> items.sortedByDescending { it.date }
    InvestmentsSortType.BY_MARKET_VALUE -> items.sortedByDescending { it.totalMarketPrice }
}
