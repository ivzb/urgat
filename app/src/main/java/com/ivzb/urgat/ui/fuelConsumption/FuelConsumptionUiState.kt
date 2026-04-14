package com.ivzb.urgat.ui.fuelConsumption

import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionCategory
import com.ivzb.urgat.domain.fuelConsumption.GroupedFuelConsumption
import com.ivzb.urgat.domain.fuelConsumption.GroupedFuelConsumptionCategories
import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.components.uiState.PagerUiState

data class FuelConsumptionUiState(
    val fuelConsumption: List<GroupedFuelConsumption> = listOf(),
    val categoriesMonthly: List<GroupedFuelConsumptionCategories> = listOf(),
    val categoriesYearly: List<GroupedFuelConsumptionCategories> = listOf(),
    val categoriesAllTime: List<FuelConsumptionCategory> = listOf(),

    override val isLoading: Boolean = false,
    override val hasError: Boolean = false,
    override val isEmpty: Boolean = fuelConsumption.isEmpty(),

    override val pages: List<Page> = FUEL_CONSUMPTION_PAGES,
    override val initialPage: Int = 0,

    override val filteringUiInfo: FuelConsumptionFilteringUiInfo = FuelConsumptionFilteringUiInfo(),
): PagerUiState