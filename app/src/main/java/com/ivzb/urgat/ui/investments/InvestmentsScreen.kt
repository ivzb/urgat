package com.ivzb.urgat.ui.investments

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ivzb.urgat.navigation.InvestmentsDestination
import com.ivzb.urgat.ui.components.LandscapePreview
import com.ivzb.urgat.ui.components.MenuScaffold
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun InvestmentsScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: InvestmentsViewModel = hiltViewModel()
) {

    InvestmentsScreenScaffold(
        onSearchQueryChanged = {
            TODO("Investments search")
        },
        navController = navController,
        modifier = modifier
    )

}

@Composable
private fun InvestmentsScreenScaffold(
    onSearchQueryChanged: (String) -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {

    MenuScaffold(
        selectedDestination = InvestmentsDestination,
        onSearchQueryChanged = onSearchQueryChanged,
        withTopAppBarActions = false,
        navController = navController,
        modifier = modifier
    ) {

        Text("Investments screen")

    }
}

@Composable
@Preview
@LandscapePreview
private fun InvestmentsScreenScaffoldPreview() {
    UrgatTheme {
        InvestmentsScreenScaffold(
            onSearchQueryChanged = {},
            navController = rememberNavController(),
            modifier = Modifier
        )
    }
}
