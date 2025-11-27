package com.ivzb.urgat.ui.addEditInvestment

data class AddEditInvestmentUiState(
    val name: String = "",
    val amount: String = "",
    val cost: String = "",
    val category: String = "",
    val currency: String = "",
    val date: Long = System.currentTimeMillis(),
    val focusIndex: Int = 0,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val withDelete: Boolean = false,
    val shouldNavigateBack: Boolean = false
)
