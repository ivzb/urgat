package com.ivzb.urgat.ui.trackers

data class TrackersUiState(
    val headerItems: List<TrackerHeaderItem> = emptyList(),

    val isLoading: Boolean = false,
    val hasError: Boolean = false,

    val uiEvent: TrackersUiEvent? = null,
)