package com.ivzb.urgat.ui.budgets

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class BudgetsFilterType: Parcelable {
    ALL_BUDGETS,
    POSITIVE_BUDGETS,
    NEGATIVE_BUDGETS,
}