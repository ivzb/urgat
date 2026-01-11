package com.ivzb.urgat.ui.components.screens.listDetail

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ivzb.urgat.navigation.UrgatRoute
import com.ivzb.urgat.ui.components.ListDetailsScaffold
import com.ivzb.urgat.ui.components.screens.UiState
import com.ivzb.urgat.ui.components.screens.urgat.UrgatScreen

@Composable
fun ListDetailScreen(
    uiState: UiState,

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
        withTopAppBarActions = withTopAppBarActions,
        topAppBarActionsContent = topAppBarActionsContent,
        listState = listState,
        modifier = modifier
    ) {


        UrgatScreen(
            uiState = uiState,
            content = content,
        )
    }
}