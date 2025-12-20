package com.ivzb.urgat.ui.investments

import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentAsset
import com.ivzb.urgat.data.investment.InvestmentCategory
import com.ivzb.urgat.data.investment.InvestmentNetWorth
import com.ivzb.urgat.ui.GroupedItems

data class InvestmentsUiState(
    val investments: List<GroupedItems<String, Investment>> = emptyList(),
    val netWorth: List<InvestmentNetWorth> = emptyList(),
    val categories: List<InvestmentCategory> = emptyList(),
    val assets: List<InvestmentAsset> = emptyList(),

    val isLoading: Boolean = false,
    val hasError: Boolean = false,

    val filteringUiInfo: InvestmentsFilteringUiInfo = InvestmentsFilteringUiInfo(),
)