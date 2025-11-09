package com.ivzb.urgat.ui.trackWeight

data class TrackWeightUiState(
    val value: String = "",
    val focusIndex: Int = 0,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val isWeightSaved: Boolean = false
)
