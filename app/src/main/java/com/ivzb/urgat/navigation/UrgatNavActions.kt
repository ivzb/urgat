package com.ivzb.urgat.navigation

import androidx.annotation.StringRes
import androidx.navigation.NavHostController
import com.ivzb.urgat.R

class UrgatNavActions(private val navController: NavHostController) {

    fun navigateToTodo() {
        navController.navigate(TodoDestination.route)
    }

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

    fun navigateToExpenses() {
        navController.navigate(ExpensesDestination.route)
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

    fun navigateToNotes() {
        navController.navigate(NotesDestination.route)
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

    fun navigateToInvestments() {
        navController.navigate(InvestmentsDestination.route)
    }

    fun navigateToAddInvestment() {
        navigateToAddEditInvestment(R.string.add_investment, null)
    }

    fun navigateToEditInvestment(expenseId: String) {
        navigateToAddEditInvestment(R.string.edit_investment, expenseId)
    }

    private fun navigateToAddEditInvestment(@StringRes title: Int, id: String?) {
        val route = "${AddEditInvestmentDestination.Args.ROUTE}/$title".let {
            if (id != null) "$it?${AddEditInvestmentDestination.Args.ARG_ID}=$id" else it
        }
        navController.navigate(route)
    }

    fun navigateToTrackWeight() {
        navController.navigate(TrackWeightDestination.route)
    }

}
