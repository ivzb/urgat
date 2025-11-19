package com.ivzb.urgat.ui.expenses

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ExpensesFilterType: Parcelable {
    ALL_EXPENSES,
    DEBIT_EXPENSES,
    COST_EXPENSES
}