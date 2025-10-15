package com.ivzb.urgat.ui.notes

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ivzb.urgat.navigation.NotesDestination
import com.ivzb.urgat.ui.components.MenuScaffold
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun NotesScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: NotesViewModel = hiltViewModel()
) {

    NotesScreenScaffold(
        onSearchQueryChanged = {
            TODO("notes search")
        },
        navController = navController,
        modifier = modifier
    )
}

@Composable
private fun NotesScreenScaffold(
    onSearchQueryChanged: (String) -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    MenuScaffold(
        currentDestination = NotesDestination,
        onSearchQueryChanged = onSearchQueryChanged,
        navController = navController,
        modifier = modifier
    ) {

        Text("Notes screen")

    }
}

@Composable
@Preview
@Preview(
    name = "Landscape",
    widthDp = 640,
    heightDp = 360
)
private fun NotesScreenScaffoldPreview() {
    UrgatTheme {
        NotesScreenScaffold(
            onSearchQueryChanged = {},
            navController = rememberNavController(),
            modifier = Modifier
        )
    }
}