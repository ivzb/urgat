package com.ivzb.urgat.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ivzb.urgat.navigation.OverviewDestination
import com.ivzb.urgat.navigation.UrgatDestination
import com.ivzb.urgat.navigation.UrgatMenuDestination
import com.ivzb.urgat.navigation.bottomNavigationScreens
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun MenuScaffold(
    selectedDestination: UrgatMenuDestination,
    onSearchQueryChanged: (String) -> Unit,
    onAddItem: (() -> Unit)? = null,

    navController: NavHostController,
    topAppBarActionsContent: (@Composable () -> Unit)? = null,

    modifier: Modifier = Modifier,
    content: @Composable (LazyListState) -> Unit
) {
    val navigationLayoutType = calculateNavigationSuiteType()

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            bottomNavigationScreens.forEach {
                item(
                    selected = it == selectedDestination,
                    onClick = {
                        navController.navigateSingleTopTo(it)
                    },
                    icon = {
                        Icon(
                            imageVector = it.icon,
                            contentDescription = stringResource(it.label)
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(it.label)
                        )
                    },
                )
            }
        },
        layoutType = navigationLayoutType,
        modifier = modifier
            .fillMaxSize()
    ) {

        UrgatScaffold(
            selectedDestination = selectedDestination,
            onSearchQueryChanged = onSearchQueryChanged,
            onAddItem = onAddItem,
            topAppBarActionsContent = topAppBarActionsContent,
            content = content
        )
    }
}

@Composable
fun UrgatScaffold(
    selectedDestination: UrgatDestination,
    onSearchQueryChanged: (String) -> Unit,
    onAddItem: (() -> Unit)? = null,
    topAppBarActionsContent: (@Composable () -> Unit)? = null,
    lazyListState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier,
    content: @Composable (LazyListState) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBarContent(
                selectedDestination = selectedDestination,
                onSearchQueryChanged = onSearchQueryChanged,
                actionsContent = topAppBarActionsContent
            )
        },
        floatingActionButton = {
            if (selectedDestination.withFAB) {
                FloatingActionButtonContent(
                    extended = lazyListState.isScrollingUp(),
                    onClick = {
                        onAddItem?.invoke()
                    }
                )
            }
        },
        contentWindowInsets = WindowInsets.systemBars,
        modifier = modifier
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .padding(innerPadding)
        ) {

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

@Composable
private fun calculateNavigationSuiteType(): NavigationSuiteType {
    val configuration = LocalConfiguration.current
    val orientation = configuration.orientation
    val windowSize = with(LocalDensity.current) {
        currentWindowSize().toSize().toDpSize()
    }

    val layoutType = when {
        // landscape phone
        windowSize.height < 600.dp && orientation == Configuration.ORIENTATION_LANDSCAPE -> {
            NavigationSuiteType.NavigationRail
        }

        // tablet
        windowSize.width >= 1200.dp -> {
            NavigationSuiteType.NavigationDrawer
        }

        else -> {
            NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(
                currentWindowAdaptiveInfo()
            )
        }
    }

    return layoutType
}

private fun NavHostController.navigateSingleTopTo(destination: UrgatMenuDestination, popInclusive: Boolean = false) =
    this.navigate(destination.route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
            inclusive = popInclusive
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }

@Preview
@LandscapePreview
@TabletPreview
@TabletLandscapePreview
@FoldablePreviewFolded
@FoldablePreviewUnfolded
@Composable
fun MenuScaffoldPreview() {
    UrgatTheme {
        MenuScaffold(
            selectedDestination = OverviewDestination,
            onSearchQueryChanged = {},
            onAddItem = {},
            navController = rememberNavController(),
            topAppBarActionsContent = {},
            modifier = Modifier
        ) { }
    }
}