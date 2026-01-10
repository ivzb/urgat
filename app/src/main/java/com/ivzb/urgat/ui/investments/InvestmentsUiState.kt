package com.ivzb.urgat.ui.investments

import com.ivzb.urgat.data.investment.InvestmentAsset
import com.ivzb.urgat.data.investment.InvestmentCategory
import com.ivzb.urgat.data.investment.InvestmentNetWorth
import com.ivzb.urgat.domain.investment.GroupedInvestmentCategories
import com.ivzb.urgat.domain.investment.GroupedInvestments
import com.ivzb.urgat.ui.listDetail.ListDetailUiState

data class InvestmentsUiState(
    val investments: List<GroupedInvestments> = emptyList(),
    val categoriesYearly: List<GroupedInvestmentCategories> = emptyList(),
    val assets: List<InvestmentAsset> = emptyList(),
    val categories: List<InvestmentCategory> = emptyList(),
    val netWorth: InvestmentNetWorth? = null,

    override val isLoading: Boolean = false,
    override val hasError: Boolean = false,
    override val isEmpty: Boolean = investments.isEmpty(),

    val initialPage: Int = 0,

    override val filteringUiInfo: InvestmentsFilteringUiInfo = InvestmentsFilteringUiInfo(),
): ListDetailUiState