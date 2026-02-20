package com.ivzb.urgat.ui.weights

import com.ivzb.urgat.data.weight.Weight
import com.ivzb.urgat.ui.components.uiState.UiState

data class WeightsUiState(
    val items: List<Weight> = listOf(),

    override val isLoading: Boolean = false,
    override val hasError: Boolean = false,
    override val isEmpty: Boolean = items.isEmpty(),

    override val filteringUiInfo: WeightsFilteringUiInfo = WeightsFilteringUiInfo(),
): UiState
