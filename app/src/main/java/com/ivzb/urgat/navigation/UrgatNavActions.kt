package com.ivzb.urgat.navigation

import androidx.annotation.StringRes
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.ivzb.urgat.R
import kotlin.math.exp

class UrgatNavActions(private val navController: NavHostController) {

    fun navigateToAddTodo() {
        navigateToAddEditTodo(R.string.add_todo, null)
    }

    fun navigateToEditTodo(todoId: String) {
        navigateToAddEditTodo(R.string.edit_todo, todoId)
    }

    private fun navigateToAddEditTodo(@StringRes title: Int, id: String?) {
        val route = "${AddEditTodoDestination.Args.ROUTE}/$title".let {
            if (id != null) "$it?${AddEditTodoDestination.Args.ARG_ID}=$id" else it
        }
        navController.navigate(route)
    }

    fun navigateToAddExpense() {
        navigateToAddEditExpense(R.string.add_expense, null)
    }

    fun navigateToEditExpense(expenseId: String) {
        navigateToAddEditExpense(R.string.edit_expense, expenseId)
    }

    private fun navigateToAddEditExpense(@StringRes title: Int, id: String?) {
        val route = "${AddEditExpenseDestination.Args.ROUTE}/$title".let {
            if (id != null) "$it?${AddEditExpenseDestination.Args.ARG_ID}=$id" else it
        }
        navController.navigate(route)
    }

    fun navigateToAddNote() {
        navigateToAddEditNote(R.string.add_note, null)
    }

    fun navigateToEditNote(expenseId: String) {
        navigateToAddEditNote(R.string.edit_note, expenseId)
    }

    private fun navigateToAddEditNote(@StringRes title: Int, id: String?) {
        val route = "${AddEditNoteDestination.Args.ROUTE}/$title".let {
            if (id != null) "$it?${AddEditNoteDestination.Args.ARG_ID}=$id" else it
        }
        navController.navigate(route)
    }
}

fun NavHostController.navigateSingleTopTo(destination: UrgatMenuDestination, popInclusive: Boolean = false) =
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
