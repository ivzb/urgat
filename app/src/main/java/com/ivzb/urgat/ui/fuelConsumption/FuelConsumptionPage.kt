package com.ivzb.urgat.ui.fuelConsumption

import androidx.annotation.StringRes
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.Page

sealed class FuelConsumptionPage(
    @StringRes
    override val titleResId: Int,
    override val isHorizontalScrollEnabled: Boolean,
): Page {
    data object Monthly: FuelConsumptionPage(R.string.monthly, false)
    data object Yearly: FuelConsumptionPage(R.string.yearly, false)
    data object AllTime: FuelConsumptionPage(R.string.all_time, true)
}

val FUEL_CONSUMPTION_PAGES = listOf(
    FuelConsumptionPage.Monthly,
    FuelConsumptionPage.Yearly,
    FuelConsumptionPage.AllTime,
)