package com.ivzb.urgat.ui.components.scaffolds

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ivzb.urgat.R
import com.ivzb.urgat.navigation.UrgatRoute
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.MoreTodosMenu
import com.ivzb.urgat.ui.components.TopAppBarContent
import com.ivzb.urgat.ui.library.LibraryRoute
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun UrgatScaffold(
    selectedRoute: UrgatRoute,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,

    withSearch: Boolean,
    navigationIcon: (@Composable () -> Unit)? = null,
    topAppBarActionsContent: (@Composable () -> Unit)? = null,
    floatingActionButtonContent: (@Composable () -> Unit)? = null,

    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBarContent(
                selectedRoute = selectedRoute,
                searchQuery = searchQuery,
                onSearchQueryChanged = onSearchQueryChanged,
                withSearch = withSearch,
                navigationIcon = navigationIcon,
                actionsContent = topAppBarActionsContent,
            )
        },
        floatingActionButton = {
            floatingActionButtonContent?.invoke()
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = modifier
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .padding(innerPadding)
        ) {

            content()
        }
    }
}

@AllDevicesPreview
@Composable
fun UrgatScaffoldPreview() {
    UrgatTheme {
        UrgatScaffold(
            selectedRoute = LibraryRoute,
            searchQuery = "",
            onSearchQueryChanged = {},

            navigationIcon = {
                IconButton(onClick = {}) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        stringResource(id = R.string.menu_back)
                    )
                }
            },
            withSearch = true,
            topAppBarActionsContent = {
                IconButton(onClick = {}) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete)
                    )
                }

                MoreTodosMenu(
                    onClearCompletedTodos = {}
                )
            },
            modifier = Modifier
        ) { }
    }
}
