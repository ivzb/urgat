package com.ivzb.urgat.ui.investments

import androidx.annotation.StringRes
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.Page

sealed class InvestmentsPage(
    @StringRes
    override val titleResId: Int,
    override val isHorizontalScrollEnabled: Boolean,
): Page {
    data object Yearly: InvestmentsPage(R.string.yearly, false)
    data object AllTime: InvestmentsPage(R.string.all_time, true)
    data object Assets: InvestmentsPage(R.string.assets, true)
}

val INVESTMENT_PAGES = listOf(
    InvestmentsPage.Yearly,
    InvestmentsPage.AllTime,
    InvestmentsPage.Assets,
)