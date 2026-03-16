package com.ivzb.urgat.ui.weights

import com.ivzb.urgat.data.weight.Weight
import com.ivzb.urgat.ui.components.uiState.UiState

data class WeightsUiState(
    val weights: List<Weight> = listOf(),

    override val isLoading: Boolean = false,
    override val hasError: Boolean = false,
    override val isEmpty: Boolean = weights.isEmpty(),

    override val filteringUiInfo: WeightsFilteringUiInfo = WeightsFilteringUiInfo(),
): UiState
