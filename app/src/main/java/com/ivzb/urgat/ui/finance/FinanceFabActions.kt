package com.ivzb.urgat.ui.finance

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingCart
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.FabAction

typealias FinanceFabAction = FabAction<FinanceFabActionId>

val FINANCE_FAB_ACTIONS = listOf(
    FabAction(
        FinanceFabActionId.Budget,
        Icons.Filled.PieChart,
        R.string.add_budget,
    ),
    FabAction(
        FinanceFabActionId.Saving,
        Icons.Filled.Savings,
        R.string.add_saving,
    ),
    FabAction(
        FinanceFabActionId.Investment,
        Icons.Filled.AttachMoney,
        R.string.add_investment,
    ),
    FabAction(
        FinanceFabActionId.Expense,
        Icons.Filled.ShoppingCart,
        R.string.add_expense,
    ),
)