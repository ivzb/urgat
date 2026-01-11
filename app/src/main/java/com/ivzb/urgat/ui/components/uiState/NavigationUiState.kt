package com.ivzb.urgat.ui.components.uiState

import com.ivzb.urgat.ui.components.FabAction

interface NavigationUiState<T>: UiState {

    val fabActions: List<FabAction<T>>

}