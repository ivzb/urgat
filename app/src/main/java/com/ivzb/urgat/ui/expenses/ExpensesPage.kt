package com.ivzb.urgat.ui.expenses

import androidx.annotation.StringRes
import com.ivzb.urgat.R

sealed class ExpensesPage(
    @StringRes
    val titleResId: Int
) {
    data object Transactions: ExpensesPage(R.string.transactions)
    data object Categories: ExpensesPage(R.string.categories)
}

val EXPENSE_PAGES = listOf(
    ExpensesPage.Transactions,
    ExpensesPage.Categories,
)