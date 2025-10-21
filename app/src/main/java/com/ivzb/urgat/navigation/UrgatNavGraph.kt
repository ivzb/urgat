package com.ivzb.urgat.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ivzb.urgat.ui.addEditExpense.AddEditExpenseScreen
import com.ivzb.urgat.ui.addEditTodo.AddEditTodoScreen
import com.ivzb.urgat.ui.expenses.ExpensesScreen
import com.ivzb.urgat.ui.notes.NotesScreen
import com.ivzb.urgat.ui.overview.OverviewScreen
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.ui.todo.TodoScreen
import com.ivzb.urgat.ui.weather.WeatherScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrgatNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    navActions: UrgatNavActions = remember(navController) {
        UrgatNavActions(navController)
    },
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {

    NavHostContent(
        navController,
        navActions,
        onShowSnackbar = { message ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(message)
            }
        },
        modifier = modifier
    )
}

@Composable
private fun NavHostContent(
    navController: NavHostController,
    navActions: UrgatNavActions,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = OverviewDestination.route,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        composable(OverviewDestination.route) {
            OverviewScreen(
                onAddTodo = { navActions.navigateToAddTodo() },
                onSeeAllTodos = { navController.navigateSingleTopTo(TodoDestination) },
                onTodoClick = { navActions.navigateToEditTodo(it.id) },

                onAddExpense = { navActions.navigateToAddExpense() },
                onSeeAllExpenses = { navController.navigateSingleTopTo(ExpensesDestination) },
                onExpenseClick = { navActions.navigateToEditExpense(it.id) },

                onAddNote = {},
                onNoteClick = {},
                navController = navController,
            )
        }

        composable(TodoDestination.route) {
            TodoScreen(
                onAddTodo = { navActions.navigateToAddTodo() },
                onTodoClick = {
                    navActions.navigateToEditTodo(it.id)
                },
                navController = navController,
            )
        }

        composable(ExpensesDestination.route) {
            ExpensesScreen(
                onAddExpense = {
                    navActions.navigateToAddExpense()
                },
                onExpenseClick = {
                    navActions.navigateToEditExpense(it.id)
                },
                navController = navController,
            )
        }

        composable(NotesDestination.route) {
            NotesScreen(
                navController = navController,
            )
        }

        composable(WeatherDestination.route) {
            WeatherScreen(
                navController = navController,
            )
        }

        composable(
            AddEditTodoDestination.route, arguments = listOf(
                navArgument(AddEditTodoDestination.Args.ARG_TITLE) { type = NavType.IntType },
                navArgument(AddEditTodoDestination.Args.ARG_ID) {
                    type = NavType.StringType; nullable = true
                },
            )
        ) { entry ->
            val title = entry.arguments?.getInt(AddEditTodoDestination.Args.ARG_TITLE) ?: 0
            val todoId = entry.arguments?.getString(AddEditTodoDestination.Args.ARG_ID)

            AddEditTodoScreen(
                screenTitle = title,
                onBack = { navController.popBackStack() },
                onShowSnackbar = onShowSnackbar
            )
        }

        composable(
            AddEditExpenseDestination.route, arguments = listOf(
                navArgument(AddEditExpenseDestination.Args.ARG_TITLE) { type = NavType.IntType },
                navArgument(AddEditExpenseDestination.Args.ARG_ID) {
                    type = NavType.StringType; nullable = true
                },
            )
        ) { entry ->
            val title = entry.arguments?.getInt(AddEditExpenseDestination.Args.ARG_TITLE) ?: 0
            val expenseId = entry.arguments?.getString(AddEditExpenseDestination.Args.ARG_ID)

            AddEditExpenseScreen(
                screenTitle = title,
                onBack = { navController.popBackStack() },
                onShowSnackbar = onShowSnackbar
            )
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
