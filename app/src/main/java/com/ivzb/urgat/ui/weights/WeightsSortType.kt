package com.ivzb.urgat.ui.weights

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class WeightsSortType: Parcelable {
    BY_DATE,
    BY_MIN,
    BY_MAX,
}