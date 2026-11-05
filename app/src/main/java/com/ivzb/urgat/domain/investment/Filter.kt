package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.ui.investments.INVESTMENTS_FILTER_TYPE_ALL

fun filterInvestments(items: List<Investment>, filterType: String): List<Investment> {
    if (filterType == INVESTMENTS_FILTER_TYPE_ALL) {
        return items
    }

    return items.filter { it.category == filterType }
}
