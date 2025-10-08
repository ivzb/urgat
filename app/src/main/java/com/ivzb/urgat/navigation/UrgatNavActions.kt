package com.ivzb.urgat.navigation

import androidx.annotation.StringRes
import androidx.navigation.NavHostController

class UrgatNavActions(private val navController: NavHostController) {

    fun navigateToTodo() {
        navController.navigateSingleTopTo(TodoDestination.route, popInclusive = true)
    }

    fun navigateToAddEditTodo(@StringRes title: Int, todoId: String?) {
        val route = "${AddEditTodoDestination.Args.ROUTE}/$title".let {
            if (todoId != null) "$it?${AddEditTodoDestination.Args.TODO_ID_ARG}=$todoId" else it
        }
        navController.navigateSingleTopTo(route)
    }

    fun navigateToAddEditExpense() {
        navController.navigateSingleTopTo(AddExpenseDestination.route)
    }
}