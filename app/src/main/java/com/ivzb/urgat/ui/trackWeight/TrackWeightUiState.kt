package com.ivzb.urgat.ui.trackWeight

data class TrackWeightUiState(
    val weight: String = "",
    val focusIndex: Int = 0,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val isWeightSaved: Boolean = false
)
