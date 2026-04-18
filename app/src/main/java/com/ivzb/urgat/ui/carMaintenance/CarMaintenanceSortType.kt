package com.ivzb.urgat.ui.carMaintenance

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class CarMaintenanceSortType: Parcelable {
    BY_DATE,
    BY_ODOMETER_MIN,
    BY_ODOMETER_MAX,
}

