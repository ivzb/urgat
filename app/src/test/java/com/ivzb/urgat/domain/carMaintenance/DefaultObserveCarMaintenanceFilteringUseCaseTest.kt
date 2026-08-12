package com.ivzb.urgat.domain.carMaintenance

import com.ivzb.urgat.R
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_ITEMS_LIMIT
import com.ivzb.urgat.data.carMaintenance.CarMaintenance
import com.ivzb.urgat.data.carMaintenance.CarMaintenanceRepository
import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceFilterType_All
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.timestamp
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultObserveCarMaintenanceFilteringUseCaseTest {

    private lateinit var repository: CarMaintenanceRepository
    private lateinit var observeUseCase: ObserveCarMaintenanceFilteringUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
        observeUseCase = DefaultObserveCarMaintenanceFilteringUseCase(repository)
    }

    @Test
    fun `invoke should return Success with all filter item followed by cars sorted by size desc`() = runTest(testDispatcher) {
        every { repository.observe(NO_ITEMS_LIMIT, NO_DATE_RANGE) } returns flowOf(carMaintenance)

        val results = observeUseCase().toList()

        advanceUntilIdle()

        val expected = listOf(
            FilterMenuItem(type = CarMaintenanceFilterType_All, labelResId = R.string.filter_all, size = 5),
            FilterMenuItem(type = "Mercedes w140", label = "Mercedes w140", size = 3),
            FilterMenuItem(type = "BMW e39", label = "BMW e39", size = 2),
        )

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        assertEquals(expected, (first as Async.Success).data)
    }

    @Test
    fun `invoke should return empty menu when repository has no items`() = runTest(testDispatcher) {
        every { repository.observe(NO_ITEMS_LIMIT, NO_DATE_RANGE) } returns flowOf(listOf())

        val results = observeUseCase().toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        assertEquals(listOf(), (first as Async.Success).data)
    }

    @Test
    fun `invoke should return Error when repository throws`() = runTest(testDispatcher) {
        every { repository.observe(NO_ITEMS_LIMIT, NO_DATE_RANGE) } returns flow {
            throw RuntimeException("Test error")
        }

        val results = observeUseCase().toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Error)
    }

    companion object {

        private val carMaintenance = listOf(
            CarMaintenance("1", "Oil change", "ABC123", null, "Mercedes w140", 150000, timestamp(2025, 11, 25), false, timestamp(2025, 11, 25)),
            CarMaintenance("2", "Brake pads", "DEF456", null, "Mercedes w140", 152000, timestamp(2025, 11, 27), false, timestamp(2025, 11, 27)),
            CarMaintenance("3", "Coolant", null, null, "Mercedes w140", 153000, timestamp(2025, 11, 28), false, timestamp(2025, 11, 28)),

            CarMaintenance("4", "Air filter", "GHI789", null, "BMW e39", 90000, timestamp(2025, 12, 9), false, timestamp(2025, 12, 9)),
            CarMaintenance("5", "Spark plugs", null, null, "BMW e39", 95000, timestamp(2025, 12, 17), false, timestamp(2025, 12, 17)),
        )

    }

}

