package com.ivzb.urgat.ui.addEditExpense

data class AddEditExpenseUiState(
    val name: String = "",
    val amount: String = "",
    val category: String = "",
    val date: Long = System.currentTimeMillis(),
    val focusIndex: Int = 0,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val withDelete: Boolean = false,
    val shouldNavigateBack: Boolean = false
)
