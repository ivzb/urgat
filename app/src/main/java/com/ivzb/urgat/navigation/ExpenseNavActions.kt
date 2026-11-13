package com.ivzb.urgat.navigation

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.addEditExpense.AddEditExpenseRoute
import com.ivzb.urgat.ui.expenses.ExpensesRoute

fun UrgatNavActions.navigateToExpenses() {
    navigator.navigate(ExpensesRoute)
}

fun UrgatNavActions.navigateToAddExpense() {
    navigator.navigate(
        AddEditExpenseRoute(
            id = null,
            titleResId = R.string.add_expense
        )
    )
}

fun UrgatNavActions.navigateToEditExpense(expenseId: String) {
    navigator.navigate(
        AddEditExpenseRoute(
            id = expenseId,
            titleResId = R.string.edit_expense
        )
    )
}
