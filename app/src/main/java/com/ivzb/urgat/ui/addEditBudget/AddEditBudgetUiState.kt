package com.ivzb.urgat.ui.addEditBudget

import androidx.annotation.StringRes

data class AddEditBudgetUiState(
    @StringRes
    val screenTitleResId: Int? = null,
    val income: String = "",
    val bankStart: String = "",
    val bankEnd: String = "",
    val currency: String = "",
    val date: Long = System.currentTimeMillis(),
    val focusIndex: Int = 0,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val withDelete: Boolean = false,
    val withDuplicate: Boolean = false,
    val shouldNavigateBack: Boolean = false
)
