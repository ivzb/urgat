package com.ivzb.urgat.ui.investments

import androidx.annotation.StringRes
import com.ivzb.urgat.R

sealed class InvestmentsPage(
    @StringRes
    val titleResId: Int
) {
    data object Transactions: InvestmentsPage(R.string.transactions)
    data object Assets: InvestmentsPage(R.string.assets)
    data object Categories: InvestmentsPage(R.string.categories)
    data object NetWorth: InvestmentsPage(R.string.net_worth)
}

val INVESTMENT_PAGES = listOf(
    InvestmentsPage.Transactions,
    InvestmentsPage.Assets,
    InvestmentsPage.Categories,
    InvestmentsPage.NetWorth,
)