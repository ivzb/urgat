package com.ivzb.urgat.ui.finance

import androidx.annotation.StringRes
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.Page

sealed class FinancePage(
    @StringRes
    override val titleResId: Int,
    override val isHorizontalScrollEnabled: Boolean,
): Page {
    data object Dashboard: FinancePage(R.string.dashboard, true)
    data object Expenses: FinancePage(R.string.expenses, true)
    data object Investments: FinancePage(R.string.investments, true)
    data object Savings: FinancePage(R.string.savings, true)
    data object Budgets: FinancePage(R.string.budgets, true)
}

val FINANCE_PAGES = listOf(
    FinancePage.Dashboard,
    FinancePage.Expenses,
    FinancePage.Investments,
    FinancePage.Savings,
    FinancePage.Budgets,
)