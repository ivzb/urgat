package com.ivzb.urgat.ui.trackers

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ivzb.urgat.navigation.TrackersDestination
import com.ivzb.urgat.ui.components.LandscapePreview
import com.ivzb.urgat.ui.components.MenuScaffold
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun TrackersScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {

    TrackersScreenScaffold(
        onSearchQueryChanged = {
            TODO("search")
        },
        navController = navController,
        modifier = modifier
    )
}

@Composable
private fun TrackersScreenScaffold(
    onSearchQueryChanged: (String) -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {

    MenuScaffold(
        selectedDestination = TrackersDestination,
        onSearchQueryChanged = onSearchQueryChanged,
        withTopAppBarActions = false,
        navController = navController,
        modifier = modifier
    ) {

        Text("trackers screen")

    }
}

@Composable
@Preview
@LandscapePreview
private fun TrackersScreenScaffoldPreview() {
    UrgatTheme {
        TrackersScreenScaffold(
            onSearchQueryChanged = {},
            navController = rememberNavController(),
            modifier = Modifier
        )
    }
}
