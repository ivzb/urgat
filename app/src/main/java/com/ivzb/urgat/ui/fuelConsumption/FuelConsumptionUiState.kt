package com.ivzb.urgat.ui.fuelConsumption

import com.ivzb.urgat.domain.fuelConsumption.GroupedFuelConsumption
import com.ivzb.urgat.ui.components.uiState.UiState

data class FuelConsumptionUiState(
    val fuelConsumption: List<GroupedFuelConsumption> = listOf(),

    override val isLoading: Boolean = false,
    override val hasError: Boolean = false,
    override val isEmpty: Boolean = fuelConsumption.isEmpty(),

    override val filteringUiInfo: FuelConsumptionFilteringUiInfo = FuelConsumptionFilteringUiInfo(),
): UiState