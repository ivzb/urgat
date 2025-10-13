package com.ivzb.urgat.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import com.ivzb.urgat.R

sealed interface UrgatDestination {
    val icon: ImageVector
    val route: String
}

sealed class UrgatMenuDestination: UrgatDestination {
    abstract val label: Int
    open val withFAB: Boolean = true
}

data object OverviewDestination: UrgatMenuDestination() {
    override val label = R.string.overview
    override val icon = Icons.Filled.Home
    override val route = "overview"
    override val withFAB = false
}

data object TodoDestination: UrgatMenuDestination() {
    override val label = R.string.todo
    override val icon = Icons.Filled.Done
    override val route = "todo"
}

data object ExpensesDestination: UrgatMenuDestination() {
    override val label = R.string.expenses
    override val icon = Icons.Filled.ShoppingCart
    override val route = "expenses"
}

data object NotesDestination: UrgatMenuDestination() {
    override val label = R.string.notes
    override val icon = Icons.Filled.Person
    override val route = "notes"
}

data object WeatherDestination: UrgatMenuDestination() {
    override val label = R.string.weather
    override val icon = Icons.Filled.DateRange
    override val route = "weather"
}

data object AddEditTodoDestination: UrgatDestination {
    override val icon = Icons.Filled.Add
    override val route = "${Args.ROUTE}/{${Args.ARG_TITLE}}?${Args.ARG_TODO_ID}={${Args.ARG_TODO_ID}}"

    object Args {
        const val ROUTE = "addEditTodo"
        const val ARG_TITLE = "title"
        const val ARG_TODO_ID = "todoId"
    }
}

data object AddExpenseDestination: UrgatDestination {
    override val icon = Icons.Filled.Add
    override val route = "addExpense"
}

// Screens to be displayed in the top UrgatTabRow
val bottomNavigationScreens = listOf(OverviewDestination, TodoDestination, ExpensesDestination, NotesDestination, WeatherDestination)