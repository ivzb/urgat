package com.ivzb.urgat.navigation

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.addEditBudget.AddEditBudgetRoute
import com.ivzb.urgat.ui.addEditExpense.AddEditExpenseRoute
import com.ivzb.urgat.ui.addEditInvestment.AddEditInvestmentRoute
import com.ivzb.urgat.ui.addEditNote.AddEditNoteRoute
import com.ivzb.urgat.ui.addEditSaving.AddEditSavingRoute
import com.ivzb.urgat.ui.addEditTodo.AddEditTodoRoute
import com.ivzb.urgat.ui.expenses.ExpensesRoute
import com.ivzb.urgat.ui.investments.InvestmentsRoute
import com.ivzb.urgat.ui.addEditWeight.AddEditWeightRoute
import com.ivzb.urgat.ui.budgets.BudgetsRoute
import com.ivzb.urgat.ui.savings.SavingsRoute
import com.ivzb.urgat.ui.weights.WeightsRoute

class UrgatNavActions(private val navigator: Navigator) {

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

    fun navigateToSavings() {
        navigator.navigate(SavingsRoute)
    }

    fun navigateToAddSaving() {
        navigator.navigate(AddEditSavingRoute(
            id = null,
            titleResId = R.string.add_saving
        ))
    }

    fun navigateToEditSaving(savingId: String) {
        navigator.navigate(AddEditSavingRoute(
            id = savingId,
            titleResId = R.string.edit_saving
        ))
    }

    fun navigateToBudgets() {
        navigator.navigate(BudgetsRoute)
    }

    fun navigateToAddBudget() {
        navigator.navigate(AddEditBudgetRoute(
            id = null,
            titleResId = R.string.add_budget,
        ))
    }

    fun navigateToEditBudget(budgetId: String) {
        navigator.navigate(AddEditBudgetRoute(
            id = budgetId,
            titleResId = R.string.edit_budget,
        ))
    }

    fun navigateToWeights() {
        navigator.navigate(WeightsRoute)
    }

    fun navigateToAddWeight() {
        navigator.navigate(AddEditWeightRoute(
            id = null,
            titleResId = R.string.add_weight,
        ))
    }

    fun navigateToEditWeight(weightId: String) {
        navigator.navigate(AddEditWeightRoute(
            id = weightId,
            titleResId = R.string.edit_weight,
        ))
    }


}
