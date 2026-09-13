package com.ivzb.urgat.data.car

import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceSortType
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionSortType
import kotlinx.coroutines.flow.Flow

interface CarRepository {

    val searchQueryFlow: Flow<String>
    suspend fun updateSearchQuery(value: String)

    val fuelConsumptionFilterTypeFlow: Flow<String>
    suspend fun updateFuelConsumptionFilterType(value: String)

    val fuelConsumptionCarFilterFlow: Flow<String>
    suspend fun updateFuelConsumptionCarFilter(value: String)

    val fuelConsumptionSortTypeFlow: Flow<FuelConsumptionSortType>
    suspend fun updateFuelConsumptionSortType(value: FuelConsumptionSortType)

    val carMaintenanceFilterTypeFlow: Flow<String>
    suspend fun updateCarMaintenanceFilterType(value: String)

    val carMaintenanceSortTypeFlow: Flow<CarMaintenanceSortType>
    suspend fun updateCarMaintenanceSortType(value: CarMaintenanceSortType)

}

