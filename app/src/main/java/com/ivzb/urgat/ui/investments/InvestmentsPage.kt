package com.ivzb.urgat.ui.investments

import androidx.annotation.StringRes
import com.ivzb.urgat.R

sealed class InvestmentsPage(
    @StringRes
    val titleResId: Int
) {
    data object Transactions: InvestmentsPage(R.string.transactions)
    data object Yearly: InvestmentsPage(R.string.yearly)
    data object NetWorth: InvestmentsPage(R.string.net_worth)
}

val INVESTMENT_PAGES = listOf(
    InvestmentsPage.Transactions,
    InvestmentsPage.Yearly,
    InvestmentsPage.NetWorth,
)