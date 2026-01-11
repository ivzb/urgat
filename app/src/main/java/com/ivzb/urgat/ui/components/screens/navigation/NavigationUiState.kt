package com.ivzb.urgat.ui.components.screens.navigation

import com.ivzb.urgat.ui.components.FabAction
import com.ivzb.urgat.ui.components.screens.UiState

interface NavigationUiState<T>: UiState {

    val fabActions: List<FabAction<T>>

}