package com.ivzb.urgat.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
    content: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val orientation = configuration.orientation

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
                    onAddItem = {
                        onAddItem?.invoke()
                    }
                )
            }
        },
        contentWindowInsets = WindowInsets.systemBars,
    ) { innerPadding ->

        Row(modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {

            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                NavigationRailContent(
                    currentDestination = currentDestination,
                    onItemClick = {
                        navController.navigate(it.route)
                    }
                )
            }

            content()
        }
    }
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