package com.ivzb.urgat.navigation

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.addEditBudget.AddEditBudgetRoute
import com.ivzb.urgat.ui.addEditCarMaintenance.AddEditCarMaintenanceRoute
import com.ivzb.urgat.ui.addEditExpense.AddEditExpenseRoute
import com.ivzb.urgat.ui.addEditFuelConsumption.AddEditFuelConsumptionRoute
import com.ivzb.urgat.ui.addEditInvestment.AddEditInvestmentRoute
import com.ivzb.urgat.ui.addEditNote.AddEditNoteRoute
import com.ivzb.urgat.ui.addEditSaving.AddEditSavingRoute
import com.ivzb.urgat.ui.addEditTodo.AddEditTodoRoute
import com.ivzb.urgat.ui.addEditWatchlistAsset.AddEditWatchlistAssetRoute
import com.ivzb.urgat.ui.expenses.ExpensesRoute
import com.ivzb.urgat.ui.investments.InvestmentsRoute
import com.ivzb.urgat.ui.addEditWeight.AddEditWeightRoute
import com.ivzb.urgat.ui.budgets.BudgetsRoute
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionRoute
import com.ivzb.urgat.ui.savings.SavingsRoute

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

    fun navigateToEditNote(noteId: String) {
        navigator.navigate(AddEditNoteRoute(
            id = noteId,
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

    fun navigateToEditInvestment(investmentId: String) {
        navigator.navigate(AddEditInvestmentRoute(
            id = investmentId,
            titleResId = R.string.edit_investment
        ))
    }

    fun navigateToTrades() {
        // todo:
//        navigator.navigate(TradesRoute)
    }

    fun navigateToAddTrade() {
        // todo:
//        navigator.navigate(AddEditTradeRoute(
//            id = null,
//            titleResId = R.string.add_trade
//        ))
    }

    fun navigateToEditTrade(tradeId: String) {
        // todo:
//        navigator.navigate(AddEditTradeRoute(
//            id = tradeId,
//            titleResId = R.string.edit_trade
//        ))
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

    fun navigateToWatchlistAssets() {
        // TODO
    }

    fun navigateToAddWatchlistAsset() {
        navigator.navigate(AddEditWatchlistAssetRoute(
            id = null,
            titleResId = R.string.add_watchlist_asset,
        ))
    }

    fun navigateToEditWatchlistAsset(watchlistAssetId: String) {
        navigator.navigate(AddEditWatchlistAssetRoute(
            id = watchlistAssetId,
            titleResId = R.string.edit_watchlist_asset,
        ))
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

    fun navigateToAddFuelConsumption() {
        navigator.navigate(AddEditFuelConsumptionRoute(
            id = null,
            titleResId = R.string.add_fuel_consumption,
        ))
    }

    fun navigateToEditFuelConsumption(fuelConsumptionId: String) {
        navigator.navigate(AddEditFuelConsumptionRoute(
            id = fuelConsumptionId,
            titleResId = R.string.edit_fuel_consumption,
        ))
    }

    fun navigateToFuelConsumption() {
        navigator.navigate(FuelConsumptionRoute)
    }

    fun navigateToAddCarMaintenance() {
        navigator.navigate(AddEditCarMaintenanceRoute(
            id = null,
            titleResId = R.string.add_car_maintenance,
        ))
    }

    fun navigateToEditCarMaintenance(carMaintenanceId: String) {
        navigator.navigate(AddEditCarMaintenanceRoute(
            id = carMaintenanceId,
            titleResId = R.string.edit_car_maintenance,
        ))
    }

    fun navigateToCarMaintenance() {
        // todo
    }

}
