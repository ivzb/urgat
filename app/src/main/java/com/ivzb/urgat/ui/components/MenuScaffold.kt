package com.ivzb.urgat.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBarContent(
                currentDestination = currentDestination,
                onSearchQueryChanged = onSearchQueryChanged
            )
        },
        bottomBar = {
            BottomBarContent(
                currentDestination = currentDestination,
                onItemClick = {
                    navController.navigate(it.route)
                }
            )
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
    ) { innerPadding ->

        Box(modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {

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