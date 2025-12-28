package com.ivzb.urgat.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.ivzb.urgat.R
import com.ivzb.urgat.navigation.BOTTOM_NAVIGATION_SCREENS
import com.ivzb.urgat.navigation.FakeNavigator
import com.ivzb.urgat.navigation.Navigator
import com.ivzb.urgat.navigation.UrgatRootRoute
import com.ivzb.urgat.navigation.UrgatRoute
import com.ivzb.urgat.navigation.drawer.URGAT_NAVIGATION_DRAWER
import com.ivzb.urgat.ui.library.LibraryFabActionId
import com.ivzb.urgat.ui.library.LibraryRoute
import com.ivzb.urgat.ui.theme.UrgatTheme
import kotlinx.coroutines.launch

@Composable
fun <T> NavigationScaffold(
    selectedRoute: UrgatRootRoute,
    onSearchQueryChanged: (String) -> Unit,

    fabActions: List<FabAction<T>>? = null,
    onFabActionClick: ((T) -> Unit)? = null,

    navigator: Navigator,
    withTopAppBarActions: Boolean,
    topAppBarActionsContent: (@Composable () -> Unit)? = null,

    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
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
        },
    ) {
        val navigationLayoutType = calculateNavigationSuiteType()

        NavigationSuiteScaffold(
            navigationSuiteItems = {
                BOTTOM_NAVIGATION_SCREENS.forEach {
                    item(
                        selected = it.route == selectedRoute,
                        onClick = {
                            navigator.navigate(it.route)
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
                selectedRoute = selectedRoute,
                onSearchQueryChanged = onSearchQueryChanged,
                withTopAppBarActions = withTopAppBarActions,
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
                    fabActions?.let {
                        ExpandableFab(
                            fabActions = it,
                            onFabActionClick = { fabActionId ->
                                onFabActionClick?.invoke(fabActionId)
                            }
                        )
                    }
                },
                content = content
            )
        }
    }
}

@Composable
fun ListDetailsScaffold(
    selectedRoute: UrgatRoute,
    onSearchQueryChanged: (String) -> Unit,
    onBack: () -> Unit,
    onAddItem: () -> Unit,

    withTopAppBarActions: Boolean = true,
    topAppBarActionsContent: (@Composable () -> Unit)? = null,

    listState: LazyListState,

    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    UrgatScaffold(
        selectedRoute = selectedRoute,
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
            FloatingActionButtonContent(
                extended = listState.isScrollingUp(),
                icon = Icons.Default.Add,
                labelResId = R.string.add,
                onClick = onAddItem
            )
        },
        modifier = modifier,
        content = content
    )
}

@Composable
private fun UrgatScaffold(
    selectedRoute: UrgatRoute,
    onSearchQueryChanged: (String) -> Unit,

    navigationIcon: (@Composable () -> Unit)? = null,
    withTopAppBarActions: Boolean = true,
    topAppBarActionsContent: (@Composable () -> Unit)? = null,
    floatingActionButtonContent: (@Composable () -> Unit)? = null,

    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBarContent(
                selectedRoute = selectedRoute,
                onSearchQueryChanged = onSearchQueryChanged,
                navigationIcon = navigationIcon,
                withActions = withTopAppBarActions,
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

@AllDevicesPreview
@Composable
fun NavigationScaffoldPreview() {
    UrgatTheme {
        NavigationScaffold<LibraryFabActionId>(
            selectedRoute = LibraryRoute,
            onSearchQueryChanged = {},
            navigator = FakeNavigator(),
            withTopAppBarActions = false,
            topAppBarActionsContent = {},
            modifier = Modifier
        ) { }
    }
}

@AllDevicesPreview
@Composable
fun ListDetailsScaffoldPreview() {
    UrgatTheme {
        ListDetailsScaffold(
            selectedRoute = LibraryRoute,
            onSearchQueryChanged = {},
            onBack = {},
            onAddItem = {},
            withTopAppBarActions = false,
            topAppBarActionsContent = {},
            listState = rememberLazyListState(),
            modifier = Modifier
        ) { }
    }
}
