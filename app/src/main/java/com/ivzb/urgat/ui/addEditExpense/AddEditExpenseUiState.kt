package com.ivzb.urgat.ui.addEditExpense

import androidx.annotation.StringRes

data class AddEditExpenseUiState(
    @StringRes
    val screenTitleResId: Int? = null,
    val name: String = "",
    val amount: String = "",
    val category: String = "",
    val date: Long = System.currentTimeMillis(),
    val excludeFromAnalytics: Boolean = false,
    val focusIndex: Int = 0,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val withDelete: Boolean = false,
    val shouldNavigateBack: Boolean = false
)
