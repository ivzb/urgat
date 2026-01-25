package com.ivzb.urgat.ui.savings

import androidx.annotation.StringRes
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.Page

sealed class SavingsPage(
    @StringRes
    override val titleResId: Int,
    override val isHorizontalScrollEnabled: Boolean,
): Page {
    data object Transactions: SavingsPage(R.string.transactions, true)
    data object Monthly: SavingsPage(R.string.monthly, false)
    data object Yearly: SavingsPage(R.string.yearly, false)
    data object AllTime: SavingsPage(R.string.all_time, true)
}

val SAVING_PAGES = listOf(
    SavingsPage.Transactions,
    SavingsPage.Monthly,
    SavingsPage.Yearly,
    SavingsPage.AllTime,
)