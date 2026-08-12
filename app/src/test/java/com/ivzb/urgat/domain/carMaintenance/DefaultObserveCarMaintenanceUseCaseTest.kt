package com.ivzb.urgat.domain.carMaintenance

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.carMaintenance.CarMaintenance
import com.ivzb.urgat.data.carMaintenance.CarMaintenanceRepository
import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceSortType
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.timestamp
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultObserveCarMaintenanceUseCaseTest {

    private lateinit var repository: CarMaintenanceRepository
    private lateinit var observeUseCase: ObserveCarMaintenanceUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
        observeUseCase = DefaultObserveCarMaintenanceUseCase(repository)
    }

    @Test
    fun `invoke should return Success with items sorted by date`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("oil")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val sortTypeFlow = flowOf(CarMaintenanceSortType.BY_DATE)
        val limitFlow = flowOf(limit)

        every { repository.observe(limit, NO_DATE_RANGE, "oil") } returns flowOf(carMaintenance)

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        val expected = listOf(carMaintenance[3], carMaintenance[2], carMaintenance[1], carMaintenance[0])

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        assertEquals(expected, (first as Async.Success).data)
    }

    @Test
    fun `invoke should return Success with items sorted by odometer min`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val sortTypeFlow = flowOf(CarMaintenanceSortType.BY_ODOMETER_MIN)
        val limitFlow = flowOf(limit)

        every { repository.observe(limit, NO_DATE_RANGE) } returns flowOf(carMaintenance)

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        val expected = listOf(carMaintenance[2], carMaintenance[3], carMaintenance[0], carMaintenance[1])

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        assertEquals(expected, (first as Async.Success).data)
    }

    @Test
    fun `invoke should return Success with items sorted by odometer max`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val sortTypeFlow = flowOf(CarMaintenanceSortType.BY_ODOMETER_MAX)
        val limitFlow = flowOf(limit)

        every { repository.observe(limit, NO_DATE_RANGE) } returns flowOf(carMaintenance)

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        val expected = listOf(carMaintenance[1], carMaintenance[0], carMaintenance[3], carMaintenance[2])

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        assertEquals(expected, (first as Async.Success).data)
    }

    @Test
    fun `invoke should keep repository order when no sort type is applied`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val limitFlow = flowOf(limit)

        every { repository.observe(limit, NO_DATE_RANGE) } returns flowOf(carMaintenance)

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, null, limitFlow).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        assertEquals(carMaintenance, (first as Async.Success).data)
    }

    @Test
    fun `invoke should return Success with empty list when repository returns no items`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val limitFlow = flowOf(limit)

        every { repository.observe(limit, NO_DATE_RANGE) } returns flowOf(listOf())

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, null, limitFlow).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        assertEquals(listOf(), (first as Async.Success).data)
    }

    @Test
    fun `invoke should return Error when repository throws`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val limitFlow = flowOf(limit)

        every { repository.observe(limit, NO_DATE_RANGE) } throws RuntimeException("Test error")

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, null, limitFlow).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Error)
    }

    companion object {

        private val carMaintenance = listOf(
            CarMaintenance("1", "Oil change", "ABC123", null, "Mercedes w140", 150000, timestamp(2025, 11, 25), false, timestamp(2025, 11, 25)),
            CarMaintenance("2", "Brake pads", "DEF456", null, "Mercedes w140", 152000, timestamp(2025, 11, 27), false, timestamp(2025, 11, 27)),

            CarMaintenance("3", "Air filter", "GHI789", null, "BMW e39", 90000, timestamp(2025, 12, 9), false, timestamp(2025, 12, 9)),
            CarMaintenance("4", "Spark plugs", null, null, "BMW e39", 95000, timestamp(2025, 12, 17), false, timestamp(2025, 12, 17)),
        )

    }

}

