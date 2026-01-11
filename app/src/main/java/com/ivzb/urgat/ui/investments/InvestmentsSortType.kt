package com.ivzb.urgat.ui.investments

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class InvestmentsSortType: Parcelable {
    BY_DATE,
    BY_MARKET_VALUE,
}