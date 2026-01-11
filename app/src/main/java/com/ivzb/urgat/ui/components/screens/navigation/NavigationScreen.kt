package com.ivzb.urgat.ui.components.screens.navigation

import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ivzb.urgat.navigation.Navigator
import com.ivzb.urgat.navigation.UrgatRootRoute
import com.ivzb.urgat.ui.components.NavigationScaffold
import com.ivzb.urgat.ui.components.screens.urgat.UrgatScreen

@VisibleForTesting
@Composable
fun <T> NavigationScreen(
    uiState: NavigationUiState<T>,

    route: UrgatRootRoute,
    onSearchQueryChanged: (String) -> Unit,
    onFabActionClick: ((T) -> Unit)? = null,
    navigator: Navigator,
    withTopAppBarActions: Boolean = true,
    topAppBarActionsContent: (@Composable () -> Unit)? = null,

    content: @Composable () -> Unit,

    modifier: Modifier = Modifier
) {

    NavigationScaffold(
        selectedRoute = route,
        onSearchQueryChanged = onSearchQueryChanged,
        fabActions = uiState.fabActions,
        onFabActionClick = onFabActionClick,
        navigator = navigator,
        withTopAppBarActions = withTopAppBarActions,
        topAppBarActionsContent = topAppBarActionsContent,
        modifier = modifier
    ) {

        UrgatScreen(
            uiState = uiState,
            content = content,
        )
    }
}