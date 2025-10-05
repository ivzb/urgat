package com.ivzb.urgat.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ivzb.urgat.ui.addExpense.AddExpenseScreen
import com.ivzb.urgat.ui.expenses.ExpensesScreen
import com.ivzb.urgat.ui.notes.NotesScreen
import com.ivzb.urgat.ui.overview.OverviewScreen
import com.ivzb.urgat.ui.todo.TodoScreen
import com.ivzb.urgat.ui.weather.WeatherScreen

@Composable
fun UrgatNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = OverviewDestination.route,
        modifier = modifier
    ) {
        composable(OverviewDestination.route) {
            OverviewScreen()
        }

        composable(TodoDestination.route) {
            TodoScreen(
                onAddTodo = {
                    navController.navigateSingleTopTo(AddTodoDestination.route)
                },
                onTodoClick = {
                    TODO()
                }
            )
        }

        composable(ExpensesDestination.route) {
            ExpensesScreen(
                onAddExpense = {
                    navController.navigateSingleTopTo(AddExpenseDestination.route)
                },
                modifier = Modifier.padding(16.dp)
            )
        }

        composable(NotesDestination.route) {
            NotesScreen()
        }

        composable(WeatherDestination.route) {
            WeatherScreen()
        }

        composable(AddExpenseDestination.route) {
            AddExpenseScreen()
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
