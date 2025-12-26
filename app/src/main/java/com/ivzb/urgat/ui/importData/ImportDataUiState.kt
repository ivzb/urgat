package com.ivzb.urgat.ui.importData

data class ImportDataUiState(
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val shouldNavigateBack: Boolean = false
)