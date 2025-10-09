package com.ivzb.urgat.navigation

import androidx.annotation.StringRes
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

class UrgatNavActions(private val navController: NavHostController) {

    fun navigateToTodo() {
        navController.navigateSingleTopTo(TodoDestination, popInclusive = true)
    }

    fun navigateToAddEditTodo(@StringRes title: Int, todoId: String?) {
        val route = "${AddEditTodoDestination.Args.ROUTE}/$title".let {
            if (todoId != null) "$it?${AddEditTodoDestination.Args.TODO_ID_ARG}=$todoId" else it
        }
        navController.navigate(route)
    }

    fun navigateToAddEditExpense() {
        navController.navigate(AddExpenseDestination.route)
    }

    private fun NavHostController.navigateSingleTopTo(destination: UrgatMenuDestination, popInclusive: Boolean = false) =
        this.navigate(destination.route) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(
                this@navigateSingleTopTo.graph.findStartDestination().id
            ) {
                saveState = true
                inclusive = popInclusive
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
}