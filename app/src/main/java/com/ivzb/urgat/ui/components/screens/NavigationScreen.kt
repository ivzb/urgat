package com.ivzb.urgat.ui.components.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.ivzb.urgat.R
import com.ivzb.urgat.navigation.BOTTOM_NAVIGATION_SCREENS
import com.ivzb.urgat.navigation.FakeNavigator
import com.ivzb.urgat.navigation.Navigator
import com.ivzb.urgat.navigation.UrgatRootRoute
import com.ivzb.urgat.navigation.drawer.URGAT_NAVIGATION_DRAWER
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.ScrollAwareFAB
import com.ivzb.urgat.ui.components.scaffolds.UrgatScaffold
import com.ivzb.urgat.ui.components.uiState.UiState
import com.ivzb.urgat.ui.library.LibraryRoute
import com.ivzb.urgat.ui.library.LibraryUiState
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.util.isScrollingUp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NavigationScreen(
    uiState: UiState,

    route: UrgatRootRoute,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onFabActionClick: () -> Unit,
    navigator: Navigator,
    withSearch: Boolean,
    withFab: Boolean = true,
    topAppBarActionsContent: (@Composable () -> Unit)? = null,
    listState: LazyListState? = null,
    onSearchFocusChanged: ((Boolean) -> Unit)? = null,

    content: @Composable () -> Unit,

    modifier: Modifier = Modifier
) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawerContent(drawerState, scope, navigator)
        },
    ) {
        val navigationLayoutType = calculateNavigationSuiteType()

        NavigationSuiteScaffold(
            navigationSuiteItems = {
                bottomNavigationItems(route, navigator)
            },
            layoutType = navigationLayoutType,
            modifier = modifier
                .fillMaxSize()
                // Makes the test tags above readable by UiAutomator, which the macrobenchmarks
                // in :baselineprofile rely on to drive the navigation.
                .semantics { testTagsAsResourceId = true }
        ) {

            NavigationScreenScaffold(
                uiState = uiState,
                route = route,
                searchQuery = searchQuery,
                onSearchQueryChanged = onSearchQueryChanged,
                onFabActionClick = onFabActionClick,
                withSearch = withSearch,
                withFab = withFab,
                topAppBarActionsContent = topAppBarActionsContent,
                listState = listState,
                onSearchFocusChanged = onSearchFocusChanged,
                drawerState = drawerState,
                scope = scope,
                content = content,
            )
        }
    }
}

@Composable
private fun NavigationDrawerContent(
    drawerState: DrawerState,
    scope: CoroutineScope,
    navigator: Navigator,
) {
    ModalDrawerSheet {
        Column(
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.content_padding_horizontal))
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.menu),
                modifier = Modifier.padding(dimensionResource(R.dimen.drawer_section_title_padding)),
                style = MaterialTheme.typography.titleLarge
            )

            HorizontalDivider(
                modifier = Modifier
                    .padding(vertical = dimensionResource(R.dimen.drawer_divider_padding)),
            )

            URGAT_NAVIGATION_DRAWER.forEach { section ->
                Text(
                    text = stringResource(section.titleResId),
                    modifier = Modifier.padding(dimensionResource(R.dimen.drawer_section_title_padding)),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                section.items.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(text = stringResource(item.labelResId)) },
                        selected = false,
                        icon = { Icon(item.iconVector, contentDescription = null) },
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                navigator.navigate(item.route)
                            }
                        }
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = dimensionResource(R.dimen.drawer_divider_padding)),
                )
            }

            Spacer(Modifier.height(dimensionResource(R.dimen.vertical_margin)))
        }
    }
}

private fun NavigationSuiteScope.bottomNavigationItems(
    route: UrgatRootRoute,
    navigator: Navigator,
) {
    BOTTOM_NAVIGATION_SCREENS.forEach {
        item(
            selected = it.route == route,
            onClick = {
                navigator.navigate(it.route)
            },
            modifier = Modifier.testTag(it.testTag),
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
}

@Composable
private fun NavigationScreenScaffold(
    uiState: UiState,
    route: UrgatRootRoute,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onFabActionClick: () -> Unit,
    withSearch: Boolean,
    withFab: Boolean,
    topAppBarActionsContent: (@Composable () -> Unit)?,
    listState: LazyListState?,
    onSearchFocusChanged: ((Boolean) -> Unit)?,
    drawerState: DrawerState,
    scope: CoroutineScope,
    content: @Composable () -> Unit,
) {
    UrgatScaffold(
        selectedRoute = route,
        searchQuery = searchQuery,
        onSearchQueryChanged = onSearchQueryChanged,
        withSearch = withSearch,
        onSearchFocusChanged = onSearchFocusChanged,
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    if (drawerState.isClosed) {
                        drawerState.open()
                    } else {
                        drawerState.close()
                    }
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(R.string.menu),
                )
            }
        },
        topAppBarActionsContent = topAppBarActionsContent,
        floatingActionButtonContent = {
            if (withFab) {
                ScrollAwareFAB(
                    visible = listState?.isScrollingUp() ?: true,
                    icon = Icons.Filled.Add,
                    labelResId = R.string.add,
                    onClick = onFabActionClick,
                )
            }
        },
        content = {
            UrgatScreen(
                uiState = uiState,
                content = content,
            )
        }
    )
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

@AllDevicesPreview
@Composable
fun NavigationScreenPreview() {
    UrgatTheme {
        NavigationScreen(
            uiState = LibraryUiState(),
            route = LibraryRoute,
            searchQuery = "",
            onSearchQueryChanged = {},
            navigator = FakeNavigator(),
            withSearch = false,
            topAppBarActionsContent = {},
            listState = rememberLazyListState(),
            modifier = Modifier,
            onFabActionClick = {},
            content = {},
        )
    }
}
