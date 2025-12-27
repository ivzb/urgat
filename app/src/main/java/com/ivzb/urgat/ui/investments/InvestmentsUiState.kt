package com.ivzb.urgat.ui.investments

import com.ivzb.urgat.data.investment.InvestmentAsset
import com.ivzb.urgat.data.investment.InvestmentCategory
import com.ivzb.urgat.data.investment.InvestmentNetWorth
import com.ivzb.urgat.domain.investment.GroupedInvestments

data class InvestmentsUiState(
    val investments: List<GroupedInvestments> = emptyList(),
    val netWorth: InvestmentNetWorth? = null,
    val categories: List<InvestmentCategory> = emptyList(),
    val assets: List<InvestmentAsset> = emptyList(),

    val isLoading: Boolean = false,
    val hasError: Boolean = false,

    val filteringUiInfo: InvestmentsFilteringUiInfo = InvestmentsFilteringUiInfo(),
)