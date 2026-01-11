package com.ivzb.urgat.ui.components.screens

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ivzb.urgat.R
import com.ivzb.urgat.navigation.UrgatRoute
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.ScrollAwareFAB
import com.ivzb.urgat.ui.components.scaffolds.UrgatScaffold
import com.ivzb.urgat.ui.components.uiState.UiState
import com.ivzb.urgat.ui.library.LibraryRoute
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.ui.todos.TodosUiState

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

    UrgatScaffold(
        selectedRoute = route,
        onSearchQueryChanged = onSearchQueryChanged,
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    stringResource(id = R.string.menu_back)
                )
            }
        },
        withTopAppBarActions = withTopAppBarActions,
        topAppBarActionsContent = topAppBarActionsContent,
        floatingActionButtonContent = {
            ScrollAwareFAB(
                visible = listState.isScrollingUp(),
                icon = Icons.Default.Add,
                labelResId = R.string.add,
                onClick = onAddItem
            )
        },
        modifier = modifier,
        content = {
            UrgatScreen(
                uiState = uiState,
                content = content,
            )
        }
    )
}

/**
 * Returns whether the lazy list is currently scrolling up.
 */
@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }

    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

@AllDevicesPreview
@Composable
fun ListDetailsScreenPreview() {
    UrgatTheme {
        ListDetailScreen(
            uiState = TodosUiState(),
            route = LibraryRoute,
            onSearchQueryChanged = {},
            onBack = {},
            onAddItem = {},
            withTopAppBarActions = false,
            topAppBarActionsContent = {},
            listState = rememberLazyListState(),
            content = {},
            modifier = Modifier
        )
    }
}
