package com.ivzb.urgat.ui.notes

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ivzb.urgat.navigation.NotesDestination
import com.ivzb.urgat.ui.components.MenuScaffold

@Composable
fun NotesScreen(
    navController: NavHostController,

    modifier: Modifier = Modifier,
    viewModel: NotesViewModel = hiltViewModel()
) {

    MenuScaffold(
        currentDestination = NotesDestination,
        onSearchQueryChanged = {
            TODO("update view model")
        },
        navController = navController,
        modifier = modifier
    ) {

        Text("notes screen")

    }
}
