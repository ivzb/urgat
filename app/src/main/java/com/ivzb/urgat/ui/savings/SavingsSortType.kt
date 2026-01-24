package com.ivzb.urgat.ui.savings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class SavingsSortType: Parcelable {
    BY_DATE,
    BY_MIN,
    BY_MAX,
}