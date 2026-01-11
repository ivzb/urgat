package com.ivzb.urgat.ui.expenses

import androidx.annotation.StringRes
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.Page

sealed class ExpensesPage(
    @StringRes
    override val titleResId: Int,
    override val isHorizontalScrollEnabled: Boolean,
): Page {
    data object Transactions: ExpensesPage(R.string.transactions, true)
    data object Monthly: ExpensesPage(R.string.monthly, false)
    data object Yearly: ExpensesPage(R.string.yearly, false)
    data object AllTime: ExpensesPage(R.string.all_time, true)
}

val EXPENSE_PAGES = listOf(
    ExpensesPage.Transactions,
    ExpensesPage.Monthly,
    ExpensesPage.Yearly,
    ExpensesPage.AllTime,
)