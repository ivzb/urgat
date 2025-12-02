package com.ivzb.urgat.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Timer
import androidx.compose.ui.graphics.vector.ImageVector
import com.ivzb.urgat.R

sealed class UrgatDestination {
    abstract val label: Int
    abstract val icon: ImageVector
    abstract val route: String
    open val withFAB: Boolean = false
}

sealed class UrgatMenuDestination: UrgatDestination() {
    override val withFAB: Boolean = false
}

data object LibraryDestination: UrgatMenuDestination() {
    override val label = R.string.library
    override val icon = Icons.Filled.Home
    override val route = "library"
}

data object FinanceDestination: UrgatMenuDestination() {
    override val label = R.string.finance
    override val icon = Icons.Filled.AttachMoney
    override val route = "finance"
}

data object InvestmentsDestination: UrgatMenuDestination() {
    override val label = R.string.investments
    override val icon = Icons.Filled.AttachMoney
    override val route = "investments"
    override val withFAB = true
}

data object WellbeingDestination: UrgatMenuDestination() {
    override val label = R.string.wellbeing
    override val icon = Icons.Filled.Accessibility
    override val route = "wellbeing"
}

data object WeatherDestination: UrgatMenuDestination() {
    override val label = R.string.weather
    override val icon = Icons.Filled.DateRange
    override val route = "weather"
}

data object TodoDestination: UrgatDestination() {
    override val label = R.string.todo
    override val icon = Icons.Filled.Done
    override val route = "todo"
    override val withFAB = true
}

data object ExpensesDestination: UrgatDestination() {
    override val label = R.string.expenses
    override val icon = Icons.Filled.ShoppingCart
    override val route = "expenses"
    override val withFAB = true
}

data object NotesDestination: UrgatDestination() {
    override val label = R.string.notes
    override val icon = Icons.Filled.Person
    override val route = "notes"
    override val withFAB = true
}

data object AddEditTodoDestination: UrgatDestination() {
    override val label = R.string.add_todo
    override val icon = Icons.Filled.Add
    override val route = "${Args.ROUTE}/{${Args.ARG_TITLE}}?${Args.ARG_ID}={${Args.ARG_ID}}"

    object Args {
        const val ROUTE = "addEditTodo"
        const val ARG_TITLE = "title"
        const val ARG_ID = "id"
    }
}

data object AddEditExpenseDestination: UrgatDestination() {
    override val label = R.string.add_expense
    override val icon = Icons.Filled.Add
    override val route = "${Args.ROUTE}/{${Args.ARG_TITLE}}?${Args.ARG_ID}={${Args.ARG_ID}}"

    object Args {
        const val ROUTE = "addEditExpense"
        const val ARG_TITLE = "title"
        const val ARG_ID = "id"
    }
}

data object AddEditNoteDestination: UrgatDestination() {
    override val label = R.string.add_note
    override val icon = Icons.Filled.Add
    override val route = "${Args.ROUTE}/{${Args.ARG_TITLE}}?${Args.ARG_ID}={${Args.ARG_ID}}"

    object Args {
        const val ROUTE = "addEditNote"
        const val ARG_TITLE = "title"
        const val ARG_ID = "id"
    }
}

data object TrackWeightDestination: UrgatDestination() {
    override val label = R.string.track_weight
    override val icon = Icons.Filled.FitnessCenter
    override val route = "trackWeight"
}

data object AddEditInvestmentDestination: UrgatDestination() {
    override val label = R.string.add_investment
    override val icon = Icons.Filled.Add
    override val route = "${Args.ROUTE}/{${Args.ARG_TITLE}}?${Args.ARG_ID}={${Args.ARG_ID}}"

    object Args {
        const val ROUTE = "addEditInvestment"
        const val ARG_TITLE = "title"
        const val ARG_ID = "id"
    }
}

// Screens to be displayed in the top UrgatTabRow
val bottomNavigationScreens = listOf(
    LibraryDestination,
    FinanceDestination,
    WellbeingDestination,
//    WeatherDestination
)