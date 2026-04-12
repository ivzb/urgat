package com.ivzb.urgat.data.car

import com.ivzb.urgat.data.dataStore.FakePreferencesDataStore
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionSortType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultCarRepositoryTest {

    private lateinit var dataStore: FakePreferencesDataStore
    private lateinit var repository: DefaultCarRepository

    @Before
    fun setup() {
        dataStore = FakePreferencesDataStore()
        repository = DefaultCarRepository(dataStore)
    }

    @Test
    fun `searchQueryFlow emits default value when not set`() = runTest {
        assertEquals("", repository.searchQueryFlow.first())
    }

    @Test
    fun `updateSearchQuery updates flow value`() = runTest {
        repository.updateSearchQuery("coffee")
        assertEquals("coffee", repository.searchQueryFlow.first())
    }

    @Test
    fun `fuelConsumptionSortTypeFlow emits default value when not set`() = runTest {
        assertEquals(FuelConsumptionSortType.BY_DATE, repository.fuelConsumptionSortTypeFlow.first())
    }

    @Test
    fun `updateFuelConsumptionSortType updates flow value`() = runTest {
        repository.updateFuelConsumptionSortType(FuelConsumptionSortType.BY_MAX)
        assertEquals(FuelConsumptionSortType.BY_MAX, repository.fuelConsumptionSortTypeFlow.first())
    }

}

