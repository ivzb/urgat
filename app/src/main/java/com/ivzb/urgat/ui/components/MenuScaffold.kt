package com.ivzb.urgat.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ivzb.urgat.navigation.OverviewDestination
import com.ivzb.urgat.navigation.UrgatMenuDestination
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun MenuScaffold(
    currentDestination: UrgatMenuDestination,
    onSearchQueryChanged: (String) -> Unit,
    onAddItem: (() -> Unit)? = null,

    navController: NavHostController,
    topAppBarActionsContent: (@Composable () -> Unit)? = null,

    modifier: Modifier = Modifier,
    content: @Composable (LazyListState) -> Unit
) {
    val configuration = LocalConfiguration.current
    val orientation = configuration.orientation

    val lazyListState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBarContent(
                currentDestination = currentDestination,
                onSearchQueryChanged = onSearchQueryChanged,
                actionsContent = topAppBarActionsContent
            )
        },
        bottomBar = {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                BottomBarContent(
                    currentDestination = currentDestination,
                    onItemClick = {
                        navController.navigate(it.route)
                    }
                )
            }
        },
        floatingActionButton = {
            if (currentDestination.withFAB) {
                FloatingActionButtonContent(
                    extended = lazyListState.isScrollingUp(),
                    onClick = {
                        onAddItem?.invoke()
                    }
                )
            }
        },
        contentWindowInsets = WindowInsets.systemBars,
    ) { innerPadding ->

        Row(modifier = modifier
            .fillMaxSize()
            .padding(innerPadding),
        ) {

            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                NavigationRailContent(
                    currentDestination = currentDestination,
                    onItemClick = {
                        navController.navigate(it.route)
                    }
                )
            }

            content(lazyListState)
        }
    }
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

@Preview
@Composable
fun MenuScaffoldPreview() {
    UrgatTheme {
        MenuScaffold(
            currentDestination = OverviewDestination,
            onSearchQueryChanged = {},
            onAddItem = {},
            navController = rememberNavController(),
            modifier = Modifier
        ) { }
    }
}