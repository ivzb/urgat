package com.ivzb.urgat.ui.trackers

import com.ivzb.urgat.data.weight.Weight

data class TrackersUiState(
    val headerItems: List<TrackersHeaderItem> = emptyList(),

    val weights: List<Weight> = listOf(),

    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val filteringUiInfo: TrackersFilteringUiInfo = TrackersFilteringUiInfo(),

    val uiEvent: TrackersUiEvent? = null,
)