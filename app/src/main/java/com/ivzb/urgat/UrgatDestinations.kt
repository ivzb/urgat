package com.ivzb.urgat

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed interface UrgatDestination {
    val icon: ImageVector
    val route: String
}

data object Overview: UrgatDestination {
    override val icon = Icons.Filled.Home
    override val route = "overview"
}

data object Expenses: UrgatDestination {
    override val icon = Icons.Filled.ShoppingCart
    override val route = "expenses"
}

data object Notes: UrgatDestination {
    override val icon = Icons.Filled.Person
    override val route = "notes"
}

data object AddExpense: UrgatDestination {
    override val icon = Icons.Filled.Add
    override val route = "addExpense"
}

// Screens to be displayed in the top UrgatTabRow
val urgatTabRowScreens = listOf(Overview, Expenses, Notes)