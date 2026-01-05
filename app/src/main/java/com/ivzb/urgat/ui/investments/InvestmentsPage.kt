package com.ivzb.urgat.ui.investments

import androidx.annotation.StringRes
import com.ivzb.urgat.R

sealed class InvestmentsPage(
    @StringRes
    val titleResId: Int
) {
    data object Transactions: InvestmentsPage(R.string.transactions)
    data object Yearly: InvestmentsPage(R.string.yearly)
    data object AllTime: InvestmentsPage(R.string.all_time)
    data object Assets: InvestmentsPage(R.string.assets)
}

val INVESTMENT_PAGES = listOf(
    InvestmentsPage.Transactions,
    InvestmentsPage.Yearly,
    InvestmentsPage.AllTime,
    InvestmentsPage.Assets,
)