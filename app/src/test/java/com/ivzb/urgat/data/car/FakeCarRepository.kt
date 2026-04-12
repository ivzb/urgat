package com.ivzb.urgat.data.car

import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionSortType
import kotlinx.coroutines.flow.MutableStateFlow

class FakeCarRepository : CarRepository {

    override val searchQueryFlow = MutableStateFlow("")
    override suspend fun updateSearchQuery(value: String) { searchQueryFlow.value = value }

    override val fuelConsumptionSortTypeFlow = MutableStateFlow(FuelConsumptionSortType.BY_DATE)
    override suspend fun updateFuelConsumptionSortType(value: FuelConsumptionSortType) { fuelConsumptionSortTypeFlow.value = value }

}

