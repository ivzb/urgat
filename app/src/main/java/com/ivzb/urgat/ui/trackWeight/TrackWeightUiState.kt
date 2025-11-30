package com.ivzb.urgat.ui.trackWeight

data class TrackWeightUiState(
    val value: String = "",
    val date: Long = System.currentTimeMillis(),
    val focusIndex: Int = 0,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val isWeightSaved: Boolean = false
)
