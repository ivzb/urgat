package com.ivzb.urgat.ui.trades

import androidx.annotation.StringRes
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.Page

sealed class TradesPage(
    @StringRes
    override val titleResId: Int,
    override val isHorizontalScrollEnabled: Boolean,
): Page {
    data object Monthly: TradesPage(R.string.monthly, false)
    data object Yearly: TradesPage(R.string.yearly, false)
    data object AllTime: TradesPage(R.string.all_time, true)
}

val TRADES_PAGES = listOf(
    TradesPage.Monthly,
    TradesPage.Yearly,
    TradesPage.AllTime,
)