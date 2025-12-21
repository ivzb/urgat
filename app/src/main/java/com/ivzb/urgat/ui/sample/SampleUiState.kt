package com.ivzb.urgat.ui.sample

data class SampleUiState(
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val shouldNavigateBack: Boolean = false
)
