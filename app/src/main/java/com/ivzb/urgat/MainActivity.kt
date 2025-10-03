package com.ivzb.urgat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ivzb.urgat.ui.compontents.UrgatTabRow
import com.ivzb.urgat.ui.theme.UrgatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UrgatApp()
        }
    }
}

@Composable
fun UrgatApp() {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentScreen = urgatTabRowScreens.find { it.route == currentDestination?.route } ?: Overview

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
            }
        ) { innerPadding ->
            UrgatNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}