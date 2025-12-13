package com.ivzb.urgat.navigation

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.addEditExpense.AddEditExpenseScreen
import com.ivzb.urgat.ui.addEditInvestment.AddEditInvestmentScreen
import com.ivzb.urgat.ui.addEditNote.AddEditNoteScreen
import com.ivzb.urgat.ui.addEditTodo.AddEditTodoScreen
import com.ivzb.urgat.ui.expenses.ExpensesScreen
import com.ivzb.urgat.ui.finance.FinanceScreen
import com.ivzb.urgat.ui.investments.InvestmentsScreen
import com.ivzb.urgat.ui.notes.NotesScreen
import com.ivzb.urgat.ui.library.LibraryScreen
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.ui.todo.TodoScreen
import com.ivzb.urgat.ui.trackWeight.TrackWeightScreen
import com.ivzb.urgat.ui.wellbeing.WellbeingScreen
import com.ivzb.urgat.ui.weather.WeatherScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrgatNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    navActions: UrgatNavActions = remember(navController) {
        UrgatNavActions(navController)
    }
) {

    NavHostContent(
        navController,
        navActions,
        modifier = modifier
    )

    DoubleBackToExitApp(navController)
}

@Composable
private fun NavHostContent(
    navController: NavHostController,
    navActions: UrgatNavActions,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = LibraryDestination.route,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        composable(
            route = LibraryDestination.route
        ) {
            LibraryScreen(
                onAddTodo = { navActions.navigateToAddTodo() },
                onSeeAllTodos = { navActions.navigateToTodo() },
                onTodoClick = { navActions.navigateToEditTodo(it.id) },

                onAddNote = { navActions.navigateToAddNote() },
                onSeeAllNotes = { navActions.navigateToNotes() },
                onNoteClick = { navActions.navigateToEditNote(it.id) },

                navController = navController,
            )
        }

        composable(
            route = FinanceDestination.route
        ) {
            FinanceScreen(
                onAddExpense = { navActions.navigateToAddExpense() },
                onSeeAllExpenses = { navActions.navigateToExpenses() },
                onExpenseClick = { navActions.navigateToEditExpense(it.id) },

                onAddInvestment = { navActions.navigateToAddInvestment() },
                onSeeAllInvestments = { navActions.navigateToInvestments() },
                onInvestmentClick = { navActions.navigateToEditInvestment(it.id) },

                navController = navController,
            )
        }

        composable(
            route = TodoDestination.route
        ) {
            TodoScreen(
                onAddTodo = { navActions.navigateToAddTodo() },
                onTodoClick = { navActions.navigateToEditTodo(it.id) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = NotesDestination.route
        ) {
            NotesScreen(
                onAddNote = { navActions.navigateToAddNote() },
                onNoteClick = { navActions.navigateToEditNote(it.id) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = ExpensesDestination.route
        ) {
            ExpensesScreen(
                onAddExpense = { navActions.navigateToAddExpense() },
                onExpenseClick = { navActions.navigateToEditExpense(it.id) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = InvestmentsDestination.route
        ) {
            InvestmentsScreen(
                onAddInvestment = { navActions.navigateToAddInvestment() },
                onInvestmentClick = { navActions.navigateToEditInvestment(it.id) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = WellbeingDestination.route
        ) {
            WellbeingScreen(
                onTrackWeight = { navActions.navigateToTrackWeight() },
                navController = navController,
            )
        }

        composable(
            route = WeatherDestination.route
        ) {
            WeatherScreen(
                navController = navController,
            )
        }

        composable(
            route = AddEditTodoDestination.route,
            arguments = listOf(
                navArgument(AddEditTodoDestination.Args.ARG_TITLE) { type = NavType.IntType },
                navArgument(AddEditTodoDestination.Args.ARG_ID) {
                    type = NavType.StringType; nullable = true
                },
            )
        ) {
            AddEditTodoScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = AddEditExpenseDestination.route,
            arguments = listOf(
                navArgument(AddEditExpenseDestination.Args.ARG_TITLE) { type = NavType.IntType },
                navArgument(AddEditExpenseDestination.Args.ARG_ID) {
                    type = NavType.StringType; nullable = true
                },
            )
        ) {
            AddEditExpenseScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = AddEditNoteDestination.route,
            arguments = listOf(
                navArgument(AddEditNoteDestination.Args.ARG_TITLE) { type = NavType.IntType },
                navArgument(AddEditNoteDestination.Args.ARG_ID) {
                    type = NavType.StringType; nullable = true
                },
            )
        ) {
            AddEditNoteScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = TrackWeightDestination.route
        ) {
            TrackWeightScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = AddEditInvestmentDestination.route,
            arguments = listOf(
                navArgument(AddEditInvestmentDestination.Args.ARG_TITLE) { type = NavType.IntType },
                navArgument(AddEditInvestmentDestination.Args.ARG_ID) {
                    type = NavType.StringType; nullable = true
                },
            )
        ) {
            AddEditInvestmentScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}


@Composable
private fun DoubleBackToExitApp(
    navController: NavHostController
) {
    val context = LocalContext.current
    var backPressedOnce by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    BackHandler {
        val isAtRootLevel = bottomNavigationScreens.any { it.route == currentRoute }

        if (!isAtRootLevel) {
            navController.popBackStack()
            return@BackHandler
        }

        if (backPressedOnce) {
            (context as? Activity)?.finish()
            return@BackHandler
        }

        backPressedOnce = true
        Toast.makeText(context, context.getString(R.string.press_back_once_more_to_exit), Toast.LENGTH_SHORT).show()

        scope.launch {
            delay(2_000)
            backPressedOnce = false
        }
    }
}

@Preview
@Composable
fun UrgatNavGraphPreview() {
    UrgatTheme {
        UrgatNavGraph()
    }
}
