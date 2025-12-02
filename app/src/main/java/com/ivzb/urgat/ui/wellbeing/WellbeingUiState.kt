package com.ivzb.urgat.ui.wellbeing

import com.ivzb.urgat.data.weight.Weight
import com.ivzb.urgat.ui.weights.WeightsFilteringUiInfo

data class WellbeingUiState(
    val headerItems: List<WellbeingHeaderItem> = emptyList(),

    val weights: List<Weight> = listOf(),
    val weightsFilteringUiInfo: WeightsFilteringUiInfo = WeightsFilteringUiInfo(),

    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val filteringUiInfo: WellbeingFilteringUiInfo = WellbeingFilteringUiInfo(),

    val uiEvent: WellbeingUiEvent? = null,
)