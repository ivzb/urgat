package com.ivzb.urgat.navigation

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.addEditBudget.AddEditBudgetRoute
import com.ivzb.urgat.ui.budgets.BudgetsRoute

fun UrgatNavActions.navigateToBudgets() {
    navigator.navigate(BudgetsRoute)
}

fun UrgatNavActions.navigateToAddBudget() {
    navigator.navigate(
        AddEditBudgetRoute(
            id = null,
            titleResId = R.string.add_budget,
        )
    )
}

fun UrgatNavActions.navigateToEditBudget(budgetId: String) {
    navigator.navigate(
        AddEditBudgetRoute(
            id = budgetId,
            titleResId = R.string.edit_budget,
        )
    )
}
