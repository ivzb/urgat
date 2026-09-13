package com.ivzb.urgat.data.car

import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceFilterType_All
import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceSortType
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionFilterType_All
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionSortType
import kotlinx.coroutines.flow.MutableStateFlow

class FakeCarRepository : CarRepository {

    override val searchQueryFlow = MutableStateFlow("")
    override suspend fun updateSearchQuery(value: String) { searchQueryFlow.value = value }

    override val fuelConsumptionFilterTypeFlow = MutableStateFlow(FuelConsumptionFilterType_All)
    override suspend fun updateFuelConsumptionFilterType(value: String) { fuelConsumptionFilterTypeFlow.value = value }

    override val fuelConsumptionCarFilterFlow = MutableStateFlow(FuelConsumptionFilterType_All)
    override suspend fun updateFuelConsumptionCarFilter(value: String) { fuelConsumptionCarFilterFlow.value = value }

    override val fuelConsumptionSortTypeFlow = MutableStateFlow(FuelConsumptionSortType.BY_DATE)
    override suspend fun updateFuelConsumptionSortType(value: FuelConsumptionSortType) { fuelConsumptionSortTypeFlow.value = value }

    override val carMaintenanceFilterTypeFlow = MutableStateFlow(CarMaintenanceFilterType_All)
    override suspend fun updateCarMaintenanceFilterType(value: String) { carMaintenanceFilterTypeFlow.value = value }

    override val carMaintenanceSortTypeFlow = MutableStateFlow(CarMaintenanceSortType.BY_DATE)
    override suspend fun updateCarMaintenanceSortType(value: CarMaintenanceSortType) { carMaintenanceSortTypeFlow.value = value }

}

