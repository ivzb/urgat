package com.ivzb.urgat.ui.listDetail

import com.ivzb.urgat.ui.components.FilteringUiInfo

interface ListDetailUiState {
    val isLoading: Boolean
    val hasError: Boolean
    val isEmpty: Boolean

    val filteringUiInfo: FilteringUiInfo
}
