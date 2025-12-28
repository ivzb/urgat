package com.ivzb.urgat.ui.expenses

import androidx.annotation.StringRes
import com.ivzb.urgat.R

sealed class ExpensesPage(
    @StringRes
    val titleResId: Int
) {
    data object Transactions: ExpensesPage(R.string.transactions)
    data object Monthly: ExpensesPage(R.string.monthly)
    data object Yearly: ExpensesPage(R.string.yearly)
    data object AllTime: ExpensesPage(R.string.all_time)
}

val EXPENSE_PAGES = listOf(
    ExpensesPage.Transactions,
    ExpensesPage.Monthly,
    ExpensesPage.Yearly,
    ExpensesPage.AllTime,
)