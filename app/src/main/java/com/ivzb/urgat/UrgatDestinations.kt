package com.ivzb.urgat

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed interface UrgatDestination {
    val icon: ImageVector
    val route: String
}

data object Expenses: UrgatDestination {
    override val icon = Icons.Filled.ShoppingCart
    override val route = "expenses"
}

data object AddExpense: UrgatDestination {
    override val icon = Icons.Filled.Add
    override val route = "addExpense"
}
