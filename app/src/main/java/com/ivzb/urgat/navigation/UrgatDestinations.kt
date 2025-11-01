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

sealed class UrgatDestination {
    abstract val label: Int
    abstract val icon: ImageVector
    abstract val route: String
    open val withFAB: Boolean = false
}

sealed class UrgatMenuDestination: UrgatDestination() {
    override val withFAB: Boolean = true
}

data object OverviewDestination: UrgatMenuDestination() {
    override val label = R.string.overview
    override val icon = Icons.Filled.Home
    override val route = "overview"
    override val withFAB = false
}

data object TrackersDestination: UrgatMenuDestination() {
    override val label = R.string.trackers
    override val icon = Icons.Filled.Person
    override val route = "trackers"
    override val withFAB = false
}

data object WeatherDestination: UrgatMenuDestination() {
    override val label = R.string.weather
    override val icon = Icons.Filled.DateRange
    override val route = "weather"
    override val withFAB = false
}

data object TodoDestination: UrgatDestination() {
    override val label = R.string.todo
    override val icon = Icons.Filled.Done
    override val route = "todo"
}

data object ExpensesDestination: UrgatDestination() {
    override val label = R.string.expenses
    override val icon = Icons.Filled.ShoppingCart
    override val route = "expenses"
}

data object NotesDestination: UrgatDestination() {
    override val label = R.string.notes
    override val icon = Icons.Filled.Person
    override val route = "notes"
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

// Screens to be displayed in the top UrgatTabRow
val bottomNavigationScreens = listOf(OverviewDestination, TrackersDestination, WeatherDestination)