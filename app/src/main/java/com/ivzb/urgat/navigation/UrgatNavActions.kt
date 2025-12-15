package com.ivzb.urgat.navigation

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.addEditExpense.AddEditExpenseRoute
import com.ivzb.urgat.ui.addEditInvestment.AddEditInvestmentRoute
import com.ivzb.urgat.ui.addEditNote.AddEditNoteRoute
import com.ivzb.urgat.ui.addEditTodo.AddEditTodoRoute
import com.ivzb.urgat.ui.expenses.ExpensesRoute
import com.ivzb.urgat.ui.investments.InvestmentsRoute
import com.ivzb.urgat.ui.notes.NotesRoute
import com.ivzb.urgat.ui.todos.TodosRoute
import com.ivzb.urgat.ui.trackWeight.TrackWeightRoute

class UrgatNavActions(private val navigator: Navigator) {

    fun navigateToTodos() {
        navigator.navigate(TodosRoute)
    }

    fun navigateToAddTodo() {
        navigator.navigate(AddEditTodoRoute(
            id = null,
            titleResId = R.string.add_todo
        ))
    }

    fun navigateToEditTodo(todoId: String) {
        navigator.navigate(AddEditTodoRoute(
            id = todoId,
            titleResId = R.string.edit_todo
        ))
    }

    fun navigateToNotes() {
        navigator.navigate(NotesRoute)
    }

    fun navigateToAddNote() {
        navigator.navigate(AddEditNoteRoute(
            id = null,
            titleResId = R.string.add_note
        ))
    }

    fun navigateToEditNote(expenseId: String) {
        navigator.navigate(AddEditNoteRoute(
            id = expenseId,
            titleResId = R.string.edit_note
        ))
    }

    fun navigateToExpenses() {
        navigator.navigate(ExpensesRoute)
    }

    fun navigateToAddExpense() {
        navigator.navigate(AddEditExpenseRoute(
            id = null,
            titleResId = R.string.add_expense
        ))
    }

    fun navigateToEditExpense(expenseId: String) {
        navigator.navigate(AddEditExpenseRoute(
            id = expenseId,
            titleResId = R.string.edit_expense
        ))
    }

    fun navigateToInvestments() {
        navigator.navigate(InvestmentsRoute)
    }

    fun navigateToAddInvestment() {
        navigator.navigate(AddEditInvestmentRoute(
            id = null,
            titleResId = R.string.add_investment
        ))
    }

    fun navigateToEditInvestment(expenseId: String) {
        navigator.navigate(AddEditInvestmentRoute(
            id = expenseId,
            titleResId = R.string.edit_investment
        ))
    }

    fun navigateToTrackWeight() {
        navigator.navigate(TrackWeightRoute)
    }

}
