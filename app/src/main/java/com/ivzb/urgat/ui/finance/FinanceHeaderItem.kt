package com.ivzb.urgat.ui.finance

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ShoppingCart
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.HeaderItem

typealias FinanceHeaderItem = HeaderItem<FinanceHeaderItemId>

val FINANCE_HEADER_ITEMS = listOf(
    HeaderItem(
        FinanceHeaderItemId.Expense,
        Icons.Filled.ShoppingCart,
        R.string.add_expense
    ),
    HeaderItem(
        FinanceHeaderItemId.Investment,
        Icons.Filled.AttachMoney,
        R.string.add_investment
    ),
)