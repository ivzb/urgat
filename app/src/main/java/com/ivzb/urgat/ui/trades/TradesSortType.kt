package com.ivzb.urgat.ui.trades

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class TradesSortType: Parcelable {
    BY_DATE,
    BY_COST,
}