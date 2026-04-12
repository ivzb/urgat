package com.ivzb.urgat.data.car

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
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

    override val fuelConsumptionSortTypeFlow: Flow<FuelConsumptionSortType> = dataStoreEnumFlow(dataStore, FUEL_CONSUMPTION_SORT_TYPE, FuelConsumptionSortType.BY_DATE)

    override suspend fun updateFuelConsumptionSortType(value: FuelConsumptionSortType) {
        dataStore.edit { it[FUEL_CONSUMPTION_SORT_TYPE] = value.name }
    }

    companion object {

        private val SEARCH_QUERY = stringPreferencesKey("car_search_query")

        private val FUEL_CONSUMPTION_SORT_TYPE = stringPreferencesKey("car_car_consumption_sort_type")

    }

}