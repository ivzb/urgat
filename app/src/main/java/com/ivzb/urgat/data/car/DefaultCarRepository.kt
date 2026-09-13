package com.ivzb.urgat.data.car

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceFilterType_All
import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceSortType
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionFilterType_All
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionSortType
import com.ivzb.urgat.util.dataStoreEnumFlow
import com.ivzb.urgat.util.dataStoreStringFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultCarRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : CarRepository {

    override val searchQueryFlow: Flow<String> = dataStoreStringFlow(dataStore, SEARCH_QUERY, "")

    override suspend fun updateSearchQuery(value: String) {
        dataStore.edit { it[SEARCH_QUERY] = value }
    }

    override val fuelConsumptionFilterTypeFlow: Flow<String> = dataStoreStringFlow(dataStore, FUEL_CONSUMPTION_FILTER_TYPE, FuelConsumptionFilterType_All)

    override suspend fun updateFuelConsumptionFilterType(value: String) {
        dataStore.edit { it[FUEL_CONSUMPTION_FILTER_TYPE] = value }
    }

    override val fuelConsumptionCarFilterFlow: Flow<String> = dataStoreStringFlow(dataStore, FUEL_CONSUMPTION_CAR_FILTER, FuelConsumptionFilterType_All)

    override suspend fun updateFuelConsumptionCarFilter(value: String) {
        dataStore.edit { it[FUEL_CONSUMPTION_CAR_FILTER] = value }
    }

    override val fuelConsumptionSortTypeFlow: Flow<FuelConsumptionSortType> = dataStoreEnumFlow(dataStore, FUEL_CONSUMPTION_SORT_TYPE, FuelConsumptionSortType.BY_DATE)

    override suspend fun updateFuelConsumptionSortType(value: FuelConsumptionSortType) {
        dataStore.edit { it[FUEL_CONSUMPTION_SORT_TYPE] = value.name }
    }

    override val carMaintenanceFilterTypeFlow: Flow<String> = dataStoreStringFlow(dataStore, CAR_MAINTENANCE_FILTER_TYPE, CarMaintenanceFilterType_All)

    override suspend fun updateCarMaintenanceFilterType(value: String) {
        dataStore.edit { it[CAR_MAINTENANCE_FILTER_TYPE] = value }
    }

    override val carMaintenanceSortTypeFlow: Flow<CarMaintenanceSortType> = dataStoreEnumFlow(dataStore, CAR_MAINTENANCE_SORT_TYPE, CarMaintenanceSortType.BY_DATE)

    override suspend fun updateCarMaintenanceSortType(value: CarMaintenanceSortType) {
        dataStore.edit { it[CAR_MAINTENANCE_SORT_TYPE] = value.name }
    }

    companion object {

        private val SEARCH_QUERY = stringPreferencesKey("car_search_query")

        private val FUEL_CONSUMPTION_FILTER_TYPE = stringPreferencesKey("car_car_consumption_filter_type")

        private val FUEL_CONSUMPTION_CAR_FILTER = stringPreferencesKey("car_car_consumption_car_filter")

        private val FUEL_CONSUMPTION_SORT_TYPE = stringPreferencesKey("car_car_consumption_sort_type")

        private val CAR_MAINTENANCE_FILTER_TYPE = stringPreferencesKey("car_car_maintenance_filter_type")

        private val CAR_MAINTENANCE_SORT_TYPE = stringPreferencesKey("car_car_maintenance_sort_type")

    }

}