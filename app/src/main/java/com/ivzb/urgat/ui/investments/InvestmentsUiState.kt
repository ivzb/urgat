package com.ivzb.urgat.ui.investments

import com.ivzb.urgat.data.investment.Investment

data class InvestmentsUiState(
    val headerItems: List<InvestmentsHeaderItem> = emptyList(),

    val items: List<Investment> = listOf(),

    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val filteringUiInfo: InvestmentsFilteringUiInfo = InvestmentsFilteringUiInfo(),

    val uiEvent: InvestmentsUiEvent? = null,
)