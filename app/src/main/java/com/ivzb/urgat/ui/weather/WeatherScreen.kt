package com.ivzb.urgat.ui.weather

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ivzb.urgat.navigation.WeatherDestination
import com.ivzb.urgat.ui.components.MenuScaffold

@Composable
fun WeatherScreen(
    navController: NavHostController,

    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = hiltViewModel()
) {

    MenuScaffold(
        currentDestination = WeatherDestination,
        onSearchQueryChanged = {
            TODO("update view model")
        },
        navController = navController,
        modifier = modifier
    ) {

        Text("weather screen")

    }
}