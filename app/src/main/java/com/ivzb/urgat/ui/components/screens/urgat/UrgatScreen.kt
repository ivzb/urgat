package com.ivzb.urgat.ui.components.screens.urgat

import androidx.compose.runtime.Composable
import com.ivzb.urgat.ui.components.EmptyStateScreen
import com.ivzb.urgat.ui.components.LoadingState
import com.ivzb.urgat.ui.components.screens.UiState

@Composable
fun UrgatScreen(
    uiState: UiState,
    content: @Composable () -> Unit
) {
    when {
        uiState.isLoading -> {
            LoadingState(
                uiState.filteringUiInfo.loadingLabel
            )
        }

        uiState.hasError -> {
            EmptyStateScreen(
                label = uiState.filteringUiInfo.errorLabel,
                iconRes = uiState.filteringUiInfo.errorIconRes,
            )
        }

        else -> {
            content()
        }
    }
}