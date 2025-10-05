package com.ivzb.urgat.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed interface UrgatDestination {
    val icon: ImageVector
    val route: String
}

data object OverviewDestination: UrgatDestination {
    override val icon = Icons.Filled.Home
    override val route = "overview"
}

data object TodoDestination: UrgatDestination {
    override val icon = Icons.Filled.Done
    override val route = "todo"
}

data object ExpensesDestination: UrgatDestination {
    override val icon = Icons.Filled.ShoppingCart
    override val route = "expenses"
}

data object NotesDestination: UrgatDestination {
    override val icon = Icons.Filled.Person
    override val route = "notes"
}

data object WeatherDestination: UrgatDestination {
    override val icon = Icons.Filled.DateRange
    override val route = "weather"
}

data object AddTodoDestination: UrgatDestination {
    override val icon = Icons.Filled.Add
    override val route = "addTodo"
}

data object AddExpenseDestination: UrgatDestination {
    override val icon = Icons.Filled.Add
    override val route = "addExpense"
}

// Screens to be displayed in the top UrgatTabRow
val urgatTabRowScreens = listOf(OverviewDestination, TodoDestination, ExpensesDestination, NotesDestination, WeatherDestination)