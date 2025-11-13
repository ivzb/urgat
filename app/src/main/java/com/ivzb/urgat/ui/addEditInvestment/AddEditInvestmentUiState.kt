package com.ivzb.urgat.ui.addEditInvestment

data class AddEditInvestmentUiState(
    val name: String = "",
    val currency: String = "",
    val amount: String = "",
    val focusIndex: Int = 0,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val isInvestmentSaved: Boolean = false
)
