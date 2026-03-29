package com.ivzb.urgat.ui.addEditWatchlistAsset

import androidx.annotation.StringRes

data class AddEditWatchlistAssetUiState(
    @StringRes
    val screenTitleResId: Int? = null,
    val name: String = "",
    val focusIndex: Int = 0,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val withDelete: Boolean = false,
    val shouldNavigateBack: Boolean = false
)