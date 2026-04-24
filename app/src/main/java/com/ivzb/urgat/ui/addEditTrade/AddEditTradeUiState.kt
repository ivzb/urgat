package com.ivzb.urgat.ui.addEditTrade

import androidx.annotation.StringRes

data class AddEditTradeUiState(
    @StringRes
    val screenTitleResId: Int? = null,
    val name: String = "",
    val shares: String = "",
    val fee: String = "",
    val marketPrice: String = "",
    val currency: String = "",
    val orderType: String = "",
    val date: Long = System.currentTimeMillis(),
    val focusIndex: Int = 0,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val withDelete: Boolean = false,
    val withDuplicate: Boolean = false,
    val shouldNavigateBack: Boolean = false
)

