package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.fuelConsumption.FuelConsumption
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionCategory
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionRepository
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionTotal
import com.ivzb.urgat.data.random.RandomRepository
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionFilter
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionSortType
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
import java.math.BigDecimal
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveGroupedFuelConsumptionCategoriesYearlyUseCaseTest {

    private lateinit var repository: FuelConsumptionRepository
    private lateinit var randomRepository: RandomRepository
    private lateinit var observeUseCase: ObserveGroupedFuelConsumptionCategoriesYearlyUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
        randomRepository = mockk()
        observeUseCase = DefaultObserveGroupedFuelConsumptionCategoriesYearlyUseCase(repository, randomRepository)

        every { randomRepository.uuid() } returns TEST_UUID
    }

    @Test
    fun `invoke should return Success with yearly category groups filtered by fuel type`() = runTest(testDispatcher) {
        `invoke should return Success with yearly category groups filtered by`(FuelConsumptionFilter(type = "Diesel"))
    }

    @Test
    fun `invoke should return Success with yearly category groups filtered by car`() = runTest(testDispatcher) {
        `invoke should return Success with yearly category groups filtered by`(FuelConsumptionFilter(carName = "Mercedes w140"))
    }

    private fun `invoke should return Success with yearly category groups filtered by`(
        filter: FuelConsumptionFilter,
    ) = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("diesel")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val filterTypeFlow = flowOf(filter)
        val sortTypeFlow = flowOf(FuelConsumptionSortType.BY_DATE)
        val limitFlow = flowOf(10)
        val items = listOf(
            fuelConsumption("1", "Diesel", "45.5", timestamp(2025, 11, 25), "Mercedes w140"),
            fuelConsumption("2", "Petrol", "40.0", timestamp(2025, 11, 27), "BMW e39"),
            fuelConsumption("3", "Diesel", "42.0", timestamp(2025, 12, 9), "Mercedes w140"),
            fuelConsumption("4", "Diesel", "50.0", timestamp(2024, 12, 17), "Mercedes w140"),
        )

        every { repository.observe(10, NO_DATE_RANGE, "diesel") } returns flowOf(items)

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        val expected = listOf(
            GroupedItems(
                group = "2025",
                total = listOf(FuelConsumptionTotal("Diesel", BigDecimal("87.5"))),
                items = listOf(
                    FuelConsumptionCategory(
                        id = TEST_UUID,
                        name = "Diesel",
                        totals = listOf(FuelConsumptionTotal("Diesel", BigDecimal("87.5"))),
                        fuelConsumption = listOf(items[2], items[0]),
                    )
                ),
            ),
            GroupedItems(
                group = "2024",
                total = listOf(FuelConsumptionTotal("Diesel", BigDecimal("50.0"))),
                items = listOf(
                    FuelConsumptionCategory(
                        id = TEST_UUID,
                        name = "Diesel",
                        totals = listOf(FuelConsumptionTotal("Diesel", BigDecimal("50.0"))),
                        fuelConsumption = listOf(items[3]),
                    )
                ),
            ),
        )

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        assertEquals(expected, (first as Async.Success).data)
    }

    @Test
    fun `invoke should return Error when repository throws`() = runTest(testDispatcher) {
        every { repository.observe(10, NO_DATE_RANGE) } throws RuntimeException("Test error")

        val results = observeUseCase(
            searchQueryFlow = flowOf(""),
            dateRangeFlow = flowOf(NO_DATE_RANGE),
            filterTypeFlow = flowOf(FuelConsumptionFilter(type = "Diesel")),
            sortTypeFlow = flowOf(FuelConsumptionSortType.BY_DATE),
            limitFlow = flowOf(10),
        ).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        assert(results.first() is Async.Error)
    }

    private fun fuelConsumption(
        id: String,
        type: String,
        amount: String,
        date: Long,
        carName: String = "Mercedes w140",
    ) = FuelConsumption(
        id = id,
        amount = BigDecimal(amount),
        price = BigDecimal("2.35"),
        currency = "EUR",
        type = type,
        carName = carName,
        odometer = 150000,
        distance = 500,
        date = date,
        isDeleted = false,
        createdAt = date,
    )

    companion object {
        private const val TEST_UUID = "mock_id"
    }
}

