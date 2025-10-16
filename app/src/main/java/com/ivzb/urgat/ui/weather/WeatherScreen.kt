package com.ivzb.urgat.ui.weather

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ivzb.urgat.navigation.WeatherDestination
import com.ivzb.urgat.ui.components.MenuScaffold
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun WeatherScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = hiltViewModel()
) {

    WeatherScreenScaffold(
        onSearchQueryChanged = {
            TODO("weather search")
        },
        navController = navController,
        modifier = modifier
    )

}

@Composable
private fun WeatherScreenScaffold(
    onSearchQueryChanged: (String) -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {

    MenuScaffold(
        selectedDestination = WeatherDestination,
        onSearchQueryChanged = onSearchQueryChanged,
        navController = navController,
        modifier = modifier
    ) {

        Text("weather screen")

    }
}

@Composable
@Preview
@Preview(
    name = "Landscape",
    widthDp = 640,
    heightDp = 360
)
private fun WeatherScreenScaffoldPreview() {
    UrgatTheme {
        WeatherScreenScaffold(
            onSearchQueryChanged = {},
            navController = rememberNavController(),
            modifier = Modifier
        )
    }
}
