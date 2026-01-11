package com.ivzb.urgat.ui.wellbeing

import com.ivzb.urgat.data.weight.Weight
import com.ivzb.urgat.ui.components.uiState.NavigationUiState
import com.ivzb.urgat.ui.weights.WeightsFilteringUiInfo

data class WellbeingUiState(
    override val fabActions: List<WellbeingFabAction> = WELLBEING_FAB_ACTIONS,

    val weights: List<Weight> = listOf(),
    val weightsFilteringUiInfo: WeightsFilteringUiInfo = WeightsFilteringUiInfo(),

    override val isLoading: Boolean = false,
    override val hasError: Boolean = false,
    override val isEmpty: Boolean = false,

    override val filteringUiInfo: WellbeingFilteringUiInfo = WellbeingFilteringUiInfo(),

    val uiEvent: WellbeingUiEvent? = null,
): NavigationUiState<WellbeingFabActionId>