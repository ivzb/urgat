package com.ivzb.urgat.ui.investments

import com.ivzb.urgat.data.investment.InvestmentAsset
import com.ivzb.urgat.data.investment.InvestmentCategory
import com.ivzb.urgat.data.investment.InvestmentNetWorth
import com.ivzb.urgat.domain.investment.GroupedInvestmentCategories
import com.ivzb.urgat.domain.investment.GroupedInvestments
import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.components.uiState.PagerUiState

data class InvestmentsUiState(
    val investments: List<GroupedInvestments> = listOf(),
    val categoriesYearly: List<GroupedInvestmentCategories> = listOf(),
    val assets: List<InvestmentAsset> = listOf(),
    val categories: List<InvestmentCategory> = listOf(),
    val netWorth: InvestmentNetWorth? = null,

    override val isLoading: Boolean = false,
    override val hasError: Boolean = false,
    override val isEmpty: Boolean = investments.isEmpty(),

    override val pages: List<Page> = INVESTMENT_PAGES,
    override val initialPage: Int = 0,

    override val filteringUiInfo: InvestmentsFilteringUiInfo = InvestmentsFilteringUiInfo(),
): PagerUiState