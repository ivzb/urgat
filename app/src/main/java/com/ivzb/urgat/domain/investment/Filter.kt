package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.ui.investments.InvestmentsFilterType_All

fun filterInvestments(items: List<Investment>, filterType: String): List<Investment> {
    if (filterType == InvestmentsFilterType_All) {
        return items
    }

    return items.filter { it.category == filterType }
}