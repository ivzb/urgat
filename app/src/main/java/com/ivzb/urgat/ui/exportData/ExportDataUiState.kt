package com.ivzb.urgat.ui.exportData

data class ExportDataUiState(
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val shouldNavigateBack: Boolean = false
)
