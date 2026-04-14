package com.ivzb.urgat.data.car

import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionSortType
import kotlinx.coroutines.flow.Flow

interface CarRepository {

    val searchQueryFlow: Flow<String>
    suspend fun updateSearchQuery(value: String)

    val fuelConsumptionFilterTypeFlow: Flow<String>
    suspend fun updateFuelConsumptionFilterType(value: String)

    val fuelConsumptionSortTypeFlow: Flow<FuelConsumptionSortType>
    suspend fun updateFuelConsumptionSortType(value: FuelConsumptionSortType)

}

