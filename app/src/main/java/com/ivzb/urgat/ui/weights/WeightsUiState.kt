package com.ivzb.urgat.ui.weights

import com.ivzb.urgat.data.weight.Weight

data class WeightsUiState(
    val items: List<Weight> = emptyList(),
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val filteringUiInfo: WeightsFilteringUiInfo = WeightsFilteringUiInfo()
)
