package com.ivzb.urgat.data.car

import com.ivzb.urgat.ui.carMaintenance.CAR_MAINTENANCE_FILTER_TYPE_ALL
import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceSortType
import com.ivzb.urgat.ui.fuelConsumption.FUEL_CONSUMPTION_FILTER_TYPE_ALL
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionSortType
import kotlinx.coroutines.flow.MutableStateFlow

class FakeCarRepository : CarRepository {

    override val searchQueryFlow = MutableStateFlow("")
    override suspend fun updateSearchQuery(value: String) {
        searchQueryFlow.value = value
    }

    override val fuelConsumptionFilterTypeFlow = MutableStateFlow(FUEL_CONSUMPTION_FILTER_TYPE_ALL)
    override suspend fun updateFuelConsumptionFilterType(value: String) {
        fuelConsumptionFilterTypeFlow.value = value
    }

    override val fuelConsumptionCarFilterFlow = MutableStateFlow(FUEL_CONSUMPTION_FILTER_TYPE_ALL)
    override suspend fun updateFuelConsumptionCarFilter(value: String) {
        fuelConsumptionCarFilterFlow.value = value
    }

    override val fuelConsumptionSortTypeFlow = MutableStateFlow(FuelConsumptionSortType.BY_DATE)
    override suspend fun updateFuelConsumptionSortType(value: FuelConsumptionSortType) {
        fuelConsumptionSortTypeFlow.value = value
    }

    override val carMaintenanceFilterTypeFlow = MutableStateFlow(CAR_MAINTENANCE_FILTER_TYPE_ALL)
    override suspend fun updateCarMaintenanceFilterType(value: String) {
        carMaintenanceFilterTypeFlow.value = value
    }

    override val carMaintenanceSortTypeFlow = MutableStateFlow(CarMaintenanceSortType.BY_DATE)
    override suspend fun updateCarMaintenanceSortType(value: CarMaintenanceSortType) {
        carMaintenanceSortTypeFlow.value = value
    }

}
