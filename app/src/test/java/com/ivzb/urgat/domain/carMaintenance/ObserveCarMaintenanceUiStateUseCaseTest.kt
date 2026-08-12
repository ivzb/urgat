package com.ivzb.urgat.domain.carMaintenance

import com.ivzb.urgat.R
import com.ivzb.urgat.data.carMaintenance.CarMaintenance
import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceFilterType_All
import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceSortType
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveCarMaintenanceUiStateUseCaseTest {

    private lateinit var fakeObserveGroupedCarMaintenanceMonthlyUseCase: FakeObserveGroupedCarMaintenanceMonthlyUseCase
    private lateinit var fakeObserveCarMaintenanceFilteringUseCase: FakeObserveCarMaintenanceFilteringUseCase
    private lateinit var fakeObserveCarMaintenanceSortingUseCase: FakeObserveCarMaintenanceSortingUseCase

    private lateinit var useCase: ObserveCarMaintenanceUiStateUseCase

    @Before
    fun setUp() {
        fakeObserveGroupedCarMaintenanceMonthlyUseCase = FakeObserveGroupedCarMaintenanceMonthlyUseCase()
        fakeObserveCarMaintenanceFilteringUseCase = FakeObserveCarMaintenanceFilteringUseCase()
        fakeObserveCarMaintenanceSortingUseCase = FakeObserveCarMaintenanceSortingUseCase()

        useCase = DefaultObserveCarMaintenanceUiStateUseCase(
            fakeObserveGroupedCarMaintenanceMonthlyUseCase,
            fakeObserveCarMaintenanceFilteringUseCase,
            fakeObserveCarMaintenanceSortingUseCase,
        )
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveGroupedCarMaintenanceMonthlyUseCase.emit(Async.Loading)
        fakeObserveCarMaintenanceFilteringUseCase.emit(Async.Loading)
        fakeObserveCarMaintenanceSortingUseCase.emit(Async.Loading)

        val results = invokeUseCase()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.isLoading)
        assertFalse(uiState.hasError)
        assertTrue(uiState.carMaintenance.isEmpty())
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        emitSuccessData()

        val results = invokeUseCase()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertEquals(fakeCarMaintenance, uiState.carMaintenance)
        assertEquals(fakeFiltering, uiState.filteringUiInfo.filterMenuItems)
        assertEquals(fakeSorting, uiState.filteringUiInfo.sortMenuItems)
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveGroupedCarMaintenanceMonthlyUseCase.emit(Async.Error)
        fakeObserveCarMaintenanceFilteringUseCase.emit(Async.Error)
        fakeObserveCarMaintenanceSortingUseCase.emit(Async.Error)

        val results = invokeUseCase()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.hasError)
        assertFalse(uiState.isLoading)
    }

    @Test
    fun `hasAppliedFilters is false when no filters are applied`() = runTest {
        emitSuccessData()

        val results = invokeUseCase()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertFalse(uiState.filteringUiInfo.hasAppliedFilters)
        assertEquals("", uiState.filteringUiInfo.searchQuery)
        assertFalse(uiState.filteringUiInfo.showFilterBadge)
        assertFalse(uiState.filteringUiInfo.showSortBadge)
        assertEquals(R.string.no_car_maintenance, uiState.filteringUiInfo.emptyLabel)
        assertEquals(R.string.no_car_maintenance_message, uiState.filteringUiInfo.emptyMessage)
        assertEquals(R.string.add_car_maintenance, uiState.filteringUiInfo.emptyButtonLabel)
    }

    @Test
    fun `hasAppliedFilters is true when search query is not empty`() = runTest {
        emitSuccessData()

        val results = invokeUseCase(searchQuery = "oil")

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.filteringUiInfo.hasAppliedFilters)
        assertEquals("oil", uiState.filteringUiInfo.searchQuery)
        assertFalse(uiState.filteringUiInfo.showFilterBadge)
        assertFalse(uiState.filteringUiInfo.showSortBadge)
        assertEquals(R.string.no_car_maintenance_search, uiState.filteringUiInfo.emptyLabel)
        assertEquals(R.string.no_car_maintenance_search_message, uiState.filteringUiInfo.emptyMessage)
        assertEquals(R.string.reset_filters, uiState.filteringUiInfo.emptyButtonLabel)
    }

    @Test
    fun `hasAppliedFilters is true when filter type is applied`() = runTest {
        emitSuccessData()

        val results = invokeUseCase(filterType = "BMW e39")

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.filteringUiInfo.hasAppliedFilters)
        assertTrue(uiState.filteringUiInfo.showFilterBadge)
        assertFalse(uiState.filteringUiInfo.showSortBadge)
    }

    @Test
    fun `hasAppliedFilters is true when sort type is applied`() = runTest {
        emitSuccessData()

        val results = invokeUseCase(sortType = CarMaintenanceSortType.BY_ODOMETER_MAX)

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.filteringUiInfo.hasAppliedFilters)
        assertFalse(uiState.filteringUiInfo.showFilterBadge)
        assertTrue(uiState.filteringUiInfo.showSortBadge)
    }

    private suspend fun invokeUseCase(
        searchQuery: String = "",
        filterType: String = CarMaintenanceFilterType_All,
        sortType: CarMaintenanceSortType = CarMaintenanceSortType.BY_DATE,
    ) = useCase(flowOf(searchQuery), flowOf(filterType), flowOf(sortType)).first()

    private fun emitSuccessData() {
        fakeObserveGroupedCarMaintenanceMonthlyUseCase.emit(Async.Success(fakeCarMaintenance))
        fakeObserveCarMaintenanceFilteringUseCase.emit(Async.Success(fakeFiltering))
        fakeObserveCarMaintenanceSortingUseCase.emit(Async.Success(fakeSorting))
    }

    companion object {

        private val carMaintenance = listOf(
            CarMaintenance("1", "Oil change", "ABC123", null, "Mercedes w140", 150000, 1L, false, 1L),
        )

        private val fakeCarMaintenance = listOf(
            GroupedItems(group = "November 2025", total = 1, items = carMaintenance)
        )

        private val fakeFiltering = listOf(
            FilterMenuItem(type = CarMaintenanceFilterType_All, labelResId = R.string.filter_all, size = 1),
            FilterMenuItem(type = "Mercedes w140", label = "Mercedes w140", size = 1),
        )

        private val fakeSorting = listOf(
            SortMenuItem(type = CarMaintenanceSortType.BY_DATE, labelResId = R.string.sort_by_date),
            SortMenuItem(type = CarMaintenanceSortType.BY_ODOMETER_MIN, labelResId = R.string.sort_by_odometer_min),
            SortMenuItem(type = CarMaintenanceSortType.BY_ODOMETER_MAX, labelResId = R.string.sort_by_odometer_max),
        )

    }

}

