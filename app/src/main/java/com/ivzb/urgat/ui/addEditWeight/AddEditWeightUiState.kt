package com.ivzb.urgat.ui.addEditWeight

import androidx.annotation.StringRes

data class AddEditWeightUiState(
    @StringRes
    val screenTitleResId: Int? = null,
    val value: String = "",
    val date: Long = System.currentTimeMillis(),
    val focusIndex: Int = 0,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val withDelete: Boolean = false,
    val shouldNavigateBack: Boolean = false
)
