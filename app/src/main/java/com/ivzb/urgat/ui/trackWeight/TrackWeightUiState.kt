package com.ivzb.urgat.ui.trackWeight

import androidx.annotation.StringRes

data class TrackWeightUiState(
    @StringRes
    val screenTitleResId: Int? = null,
    val value: String = "",
    val date: Long = System.currentTimeMillis(),
    val focusIndex: Int = 0,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val isWeightSaved: Boolean = false
)
