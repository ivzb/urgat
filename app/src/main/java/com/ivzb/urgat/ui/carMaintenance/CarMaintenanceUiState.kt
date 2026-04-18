package com.ivzb.urgat.ui.carMaintenance

import com.ivzb.urgat.data.carMaintenance.CarMaintenance
import com.ivzb.urgat.domain.carMaintenance.GroupedCarMaintenance
import com.ivzb.urgat.ui.components.uiState.UiState

data class CarMaintenanceUiState(
    val carMaintenance: List<GroupedCarMaintenance> = listOf(),

    override val isLoading: Boolean = false,
    override val hasError: Boolean = false,
    override val isEmpty: Boolean = carMaintenance.isEmpty(),

    override val filteringUiInfo: CarMaintenanceFilteringUiInfo = CarMaintenanceFilteringUiInfo(),
): UiState

