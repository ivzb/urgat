package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.R
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_ITEMS_LIMIT
import com.ivzb.urgat.data.fuelConsumption.FuelConsumption
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionRepository
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionFilterType_All
import com.ivzb.urgat.util.Async
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
import java.math.BigDecimal
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveFuelConsumptionFilteringUseCaseTest {

    private lateinit var repository: FuelConsumptionRepository
    private lateinit var observeUseCase: ObserveFuelConsumptionFilteringUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
        observeUseCase = DefaultObserveFuelConsumptionFilteringUseCase(repository)
    }

    @Test
    fun `invoke should return Success with grouped fuel types sorted by size`() = runTest(testDispatcher) {
        val expected = listOf(
            FilterMenuItem(type = FuelConsumptionFilterType_All, labelResId = R.string.filter_all, size = 5),
            FilterMenuItem(type = "Diesel", label = "Diesel", size = 3),
            FilterMenuItem(type = "Petrol", label = "Petrol", size = 2),
        )

        val repositoryStream = flowOf(
            listOf(
                fuelConsumption(id = "1", type = "Diesel"),
                fuelConsumption(id = "2", type = "Diesel"),
                fuelConsumption(id = "3", type = "Petrol"),
                fuelConsumption(id = "4", type = "Petrol"),
                fuelConsumption(id = "5", type = "Diesel"),
            )
        )

        every { repository.observe(NO_ITEMS_LIMIT, NO_DATE_RANGE) } returns repositoryStream

        val results = observeUseCase().toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        assertEquals(expected, (first as Async.Success).data)
    }

    @Test
    fun `invoke should return empty menu when repository has no items`() = runTest(testDispatcher) {
        every { repository.observe(NO_ITEMS_LIMIT, NO_DATE_RANGE) } returns flowOf(emptyList())

        val results = observeUseCase().toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        assertEquals(emptyList(), (first as Async.Success).data)
    }

    @Test
    fun `invoke should return Error when repository throws`() = runTest(testDispatcher) {
        every { repository.observe(NO_ITEMS_LIMIT, NO_DATE_RANGE) } returns flow {
            throw RuntimeException("Test error")
        }

        val results = observeUseCase().toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        assert(results.first() is Async.Error)
    }

    private fun fuelConsumption(
        id: String,
        type: String,
    ) = FuelConsumption(
        id = id,
        amount = BigDecimal("45.5"),
        price = BigDecimal("2.35"),
        currency = "EUR",
        type = type,
        carName = "Mercedes w140",
        odometer = 150000,
        distance = 500,
        date = 1000L,
        isDeleted = false,
        createdAt = 1000L,
    )
}

