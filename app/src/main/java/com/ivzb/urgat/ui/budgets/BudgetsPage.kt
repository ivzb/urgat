package com.ivzb.urgat.ui.budgets

import androidx.annotation.StringRes
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.Page

sealed class BudgetsPage(
    @StringRes
    override val titleResId: Int,
    override val isHorizontalScrollEnabled: Boolean,
): Page {
    data object Monthly: BudgetsPage(R.string.monthly, false)
    data object Yearly: BudgetsPage(R.string.yearly, false)
    data object AllTime: BudgetsPage(R.string.all_time, true)
}

val BUDGET_PAGES = listOf(
    BudgetsPage.Monthly,
    BudgetsPage.Yearly,
    BudgetsPage.AllTime,
)