package com.ivzb.urgat.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.compontents.UrgatTabRow
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun UrgatNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentScreen = urgatTabRowScreens.find { it.route == currentDestination?.route } ?: OverviewDestination

    UrgatTheme {
        Scaffold(
            topBar = {
                UrgatTabRow(
                    allScreens = urgatTabRowScreens,
                    onTabSelected = { screen ->
                        navController.navigateSingleTopTo(screen.route)
                    },
                    currentScreen = currentScreen,
                )
            },
            floatingActionButton = {
                if (currentScreen == TodoDestination) {
                    FloatingActionButton(onClick = {
                        navController.navigateSingleTopTo(AddTodoDestination.route)
                    }) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.new_todo))
                    }
                }
            }
        ) { innerPadding ->
            UrgatNavHost(
                navController = navController,
                modifier = modifier.padding(innerPadding)
            )
        }
    }
}