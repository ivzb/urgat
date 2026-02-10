package com.ivzb.urgat.ui.finance

import androidx.annotation.StringRes
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.Page

sealed class FinancePage(
    @StringRes
    override val titleResId: Int,
    override val isHorizontalScrollEnabled: Boolean,
): Page {
    data object Expenses: FinancePage(R.string.expenses, true)
    data object Investments: FinancePage(R.string.investments, true)
    data object Savings: FinancePage(R.string.savings, true)
    data object Budgets: FinancePage(R.string.budgets, true)
    data object Report: FinancePage(R.string.report, true)
    data object FIRE: FinancePage(R.string.FIRE, true)
}

val FINANCE_PAGES = listOf(
    FinancePage.Expenses,
    FinancePage.Investments,
    FinancePage.Savings,
    FinancePage.Budgets,
    FinancePage.Report,
    FinancePage.FIRE,
)