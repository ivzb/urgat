package com.ivzb.urgat.ui.wellbeing

import com.ivzb.urgat.ui.components.uiState.UiState
import com.ivzb.urgat.ui.weights.WeightsUiState

data class WellbeingUiState(
    val weightsUiState: WeightsUiState = WeightsUiState(),

    override val isLoading: Boolean = false,
    override val hasError: Boolean = false,
    override val isEmpty: Boolean = false,

    override val filteringUiInfo: WellbeingFilteringUiInfo = WellbeingFilteringUiInfo(),

    val uiEvent: WellbeingUiEvent? = null,
): UiState