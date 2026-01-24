package com.ivzb.urgat.ui.addEditSaving

import androidx.annotation.StringRes

data class AddEditSavingUiState(
    @StringRes
    val screenTitleResId: Int? = null,
    val name: String = "",
    val amount: String = "",
    val date: Long = System.currentTimeMillis(),
    val focusIndex: Int = 0,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val withDelete: Boolean = false,
    val shouldNavigateBack: Boolean = false
)
