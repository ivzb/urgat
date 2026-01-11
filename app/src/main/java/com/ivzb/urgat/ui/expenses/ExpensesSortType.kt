package com.ivzb.urgat.ui.expenses

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ExpensesSortType: Parcelable {
    BY_DATE,
    BY_COST,
}