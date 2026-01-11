package com.ivzb.urgat.ui.components.screens

import com.ivzb.urgat.ui.components.FilteringUiInfo

interface UiState {
    val isLoading: Boolean
    val hasError: Boolean
    val isEmpty: Boolean

    val filteringUiInfo: FilteringUiInfo
}