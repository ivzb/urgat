package com.ivzb.urgat.ui.fuelConsumption

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class FuelConsumptionSortType: Parcelable {
    BY_DATE,
    BY_MIN,
    BY_MAX,
}

