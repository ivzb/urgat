package com.ivzb.urgat.ui.components.screens

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ivzb.urgat.R
import com.ivzb.urgat.navigation.UrgatRoute
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.scaffolds.UrgatScaffold
import com.ivzb.urgat.ui.components.uiState.UiState
import com.ivzb.urgat.ui.library.LibraryRoute
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.ui.weights.WeightsUiState

@Composable
fun ListDetailScreen(
    uiState: UiState,

    route: UrgatRoute,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onBack: () -> Unit,

    withSearch: Boolean,
    topAppBarActionsContent: (@Composable () -> Unit)? = null,
    listState: LazyListState,

    content: @Composable () -> Unit,

    modifier: Modifier = Modifier
) {

    UrgatScaffold(
        selectedRoute = route,
        searchQuery = searchQuery,
        onSearchQueryChanged = onSearchQueryChanged,
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    stringResource(id = R.string.menu_back)
                )
            }
        },
        withSearch = withSearch,
        topAppBarActionsContent = topAppBarActionsContent,
        modifier = modifier,
        content = {
            UrgatScreen(
                uiState = uiState,
                content = content,
            )
        }
    )
}

@AllDevicesPreview
@Composable
fun ListDetailsScreenPreview() {
    UrgatTheme {
        ListDetailScreen(
            uiState = WeightsUiState(),
            route = LibraryRoute,
            searchQuery = "",
            onSearchQueryChanged = {},
            onBack = {},
            withSearch = false,
            topAppBarActionsContent = {},
            listState = rememberLazyListState(),
            content = {},
            modifier = Modifier
        )
    }
}
