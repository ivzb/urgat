package com.ivzb.urgat.ui.budgets

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class BudgetsSortType: Parcelable {
    BY_DATE,
    BY_MIN,
    BY_MAX,
}