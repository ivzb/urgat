package com.ivzb.urgat.ui.budgets

import androidx.annotation.StringRes
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.Page

sealed class BudgetsPage(
    @StringRes
    override val titleResId: Int,
    override val isHorizontalScrollEnabled: Boolean,
): Page {
    data object Transactions: BudgetsPage(R.string.transactions, true)
}

val BUDGET_PAGES = listOf(
    BudgetsPage.Transactions,
)