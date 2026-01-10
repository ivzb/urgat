package com.ivzb.urgat.ui.listDetail

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ivzb.urgat.navigation.UrgatRoute
import com.ivzb.urgat.ui.components.EmptyStateScreen
import com.ivzb.urgat.ui.components.ListDetailsScaffold
import com.ivzb.urgat.ui.components.LoadingState

@Composable
fun ListDetailScreen(
    uiState: ListDetailUiState,

    route: UrgatRoute,
    onSearchQueryChanged: (String) -> Unit,
    onAddItem: () -> Unit,
    onBack: () -> Unit,

    withTopAppBarActions: Boolean = true,
    topAppBarActionsContent: (@Composable () -> Unit)? = null,
    listState: LazyListState,

    content: @Composable () -> Unit,

    modifier: Modifier = Modifier
) {

    ListDetailsScaffold(
        selectedRoute = route,
        onSearchQueryChanged = onSearchQueryChanged,
        onAddItem = onAddItem,
        onBack = onBack,
        topAppBarActionsContent = topAppBarActionsContent,
        listState = listState,
        modifier = modifier
    ) {

        when {
            uiState.isLoading -> {
                LoadingState(
                    label = uiState.filteringUiInfo.loadingLabel,
                )
            }

            uiState.hasError -> {
                EmptyStateScreen(
                    label = uiState.filteringUiInfo.errorLabel,
                    iconRes = uiState.filteringUiInfo.errorIconRes
                )
            }

            uiState.isEmpty -> {
                EmptyStateScreen(
                    label = uiState.filteringUiInfo.emptyLabel,
                    iconRes = uiState.filteringUiInfo.emptyIconRes
                )
            }

            else -> {
                content()
            }
        }
    }
}