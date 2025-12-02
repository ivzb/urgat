package com.ivzb.urgat.ui.wellbeing

import com.ivzb.urgat.data.weight.Weight

data class WellbeingUiState(
    val headerItems: List<WellbeingHeaderItem> = emptyList(),

    val weights: List<Weight> = listOf(),

    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val filteringUiInfo: WellbeingFilteringUiInfo = WellbeingFilteringUiInfo(),

    val uiEvent: WellbeingUiEvent? = null,
)