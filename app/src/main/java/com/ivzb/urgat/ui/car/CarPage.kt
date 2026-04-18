package com.ivzb.urgat.ui.car

import androidx.annotation.StringRes
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.Page

sealed class CarPage(
    @StringRes
    override val titleResId: Int,
    override val isHorizontalScrollEnabled: Boolean,
): Page {
    data object FuelConsumption: CarPage(R.string.fuel_consumption, true)
    data object Maintenance: CarPage(R.string.car_maintenance, true)
}

val CAR_PAGES = listOf(
    CarPage.FuelConsumption,
    CarPage.Maintenance,
)

