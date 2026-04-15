package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_SEARCH_QUERY
import com.ivzb.urgat.data.fuelConsumption.FuelConsumption
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionRepository
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionSortType
import com.ivzb.urgat.util.Async
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
class ObserveGroupedFuelConsumptionMonthlyUseCaseTest {

    private lateinit var repository: FuelConsumptionRepository
    private lateinit var observeUseCase: ObserveGroupedFuelConsumptionMonthlyUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
        observeUseCase = DefaultObserveGroupedFuelConsumptionMonthlyUseCase(repository)
    }

    @Test
    fun `invoke should return Success with grouped items sorted by date`() = runTest(testDispatcher) {
        `invoke should return Success with grouped items sorted by`(FuelConsumptionSortType.BY_DATE)
    }

    @Test
    fun `invoke should return Success with grouped items sorted by min`() = runTest(testDispatcher) {
        `invoke should return Success with grouped items sorted by`(FuelConsumptionSortType.BY_MIN)
    }

    @Test
    fun `invoke should return Success with grouped items sorted by max`() = runTest(testDispatcher) {
        `invoke should return Success with grouped items sorted by`(FuelConsumptionSortType.BY_MAX)
    }

    private fun `invoke should return Success with grouped items sorted by`(
        sortType: FuelConsumptionSortType,
    ) = runTest(testDispatcher) {
        val sortTypeFlow = flowOf(sortType)
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val limitFlow = flowOf(limit)

        val fc1 = makeFuelConsumption("fc_1", amount = "45.5", price = "2.35", date = 86_400_000L)  // Jan 2, 1970
        val fc2 = makeFuelConsumption("fc_2", amount = "40.0", price = "2.40", date = 172_800_000L) // Jan 3, 1970

        every { repository.observe(limit, NO_DATE_RANGE) } returns flowOf(listOf(fc1, fc2))

        val results = observeUseCase(
            searchQueryFlow = flowOf(NO_SEARCH_QUERY),
            dateRangeFlow = dateRangeFlow,
            filterTypeFlow = null,
            sortTypeFlow = sortTypeFlow,
            limitFlow = limitFlow,
        ).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val actual = (first as Async.Success).data

        // Both items should be in the same monthly group (January 1970)
        assertEquals(1, actual.size)
        val group = actual[0]
        assertEquals("January 1970", group.group)
        assertEquals(2, group.items.size)

        val expectedTotal = fc1.amount + fc2.amount
        assertEquals(1, group.total.size)
        assertEquals(expectedTotal, group.total[0].amount)
        assertEquals("Diesel", group.total[0].type)
    }

    @Test
    fun `invoke should group items by month`() = runTest(testDispatcher) {
        val sortTypeFlow = flowOf(FuelConsumptionSortType.BY_DATE)
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val limitFlow = flowOf(limit)

        // January 1970 and February 1970
        val fc1 = makeFuelConsumption("fc_1", amount = "45.5", price = "2.35", date = 86_400_000L)    // Jan 2, 1970
        val fc2 = makeFuelConsumption("fc_2", amount = "40.0", price = "2.40", date = 2_764_800_000L) // Feb 2, 1970

        every { repository.observe(limit, NO_DATE_RANGE) } returns flowOf(listOf(fc1, fc2))

        val results = observeUseCase(
            searchQueryFlow = flowOf(NO_SEARCH_QUERY),
            dateRangeFlow = dateRangeFlow,
            filterTypeFlow = null,
            sortTypeFlow = sortTypeFlow,
            limitFlow = limitFlow,
        ).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val actual = (first as Async.Success).data

        // Items from different months should be in separate groups
        assertEquals(2, actual.size)

        // Groups should be sorted descending by date
        assertEquals("February 1970", actual[0].group)
        assertEquals(1, actual[0].items.size)

        assertEquals("January 1970", actual[1].group)
        assertEquals(1, actual[1].items.size)
    }

    @Test
    fun `invoke should return empty list when repository is empty`() = runTest(testDispatcher) {
        val sortTypeFlow = flowOf(FuelConsumptionSortType.BY_DATE)
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val limitFlow = flowOf(limit)

        every { repository.observe(limit, NO_DATE_RANGE) } returns flowOf(emptyList())

        val results = observeUseCase(
            searchQueryFlow = flowOf(NO_SEARCH_QUERY),
            dateRangeFlow = dateRangeFlow,
            filterTypeFlow = null,
            sortTypeFlow = sortTypeFlow,
            limitFlow = limitFlow,
        ).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val actual = (first as Async.Success).data
        assertEquals(0, actual.size)
    }

    @Test
    fun `invoke should return Error when repository throws`() = runTest(testDispatcher) {
        val sortTypeFlow = flowOf(FuelConsumptionSortType.BY_DATE)
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val limitFlow = flowOf(limit)

        every { repository.observe(limit, NO_DATE_RANGE) } throws RuntimeException("Test error")

        val results = observeUseCase(
            searchQueryFlow = flowOf(NO_SEARCH_QUERY),
            dateRangeFlow = dateRangeFlow,
            filterTypeFlow = null,
            sortTypeFlow = sortTypeFlow,
            limitFlow = limitFlow,
        ).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Error)
    }

    @Test
    fun `invoke should compute totals per currency`() = runTest(testDispatcher) {
        val sortTypeFlow = flowOf(FuelConsumptionSortType.BY_DATE)
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val limitFlow = flowOf(limit)

        // Same month, different currencies
        val fc1 = makeFuelConsumption("fc_1", amount = "45.5", price = "2.35", currency = "EUR", date = 86_400_000L)
        val fc2 = makeFuelConsumption("fc_2", amount = "40.0", price = "2.40", currency = "USD", type = "Petrol", date = 172_800_000L)

        every { repository.observe(limit, NO_DATE_RANGE) } returns flowOf(listOf(fc1, fc2))

        val results = observeUseCase(
            searchQueryFlow = flowOf(NO_SEARCH_QUERY),
            dateRangeFlow = dateRangeFlow,
            filterTypeFlow = null,
            sortTypeFlow = sortTypeFlow,
            limitFlow = limitFlow,
        ).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val actual = (first as Async.Success).data

        assertEquals(1, actual.size)
        val group = actual[0]
        // Should have 2 totals, one per currency
        assertEquals(2, group.total.size)

        val dieselTotal = group.total.find { it.type == "Diesel" }
        val petrolTotal = group.total.find { it.type == "Petrol" }
        assertEquals(fc1.amount, dieselTotal?.amount)
        assertEquals(fc2.amount, petrolTotal?.amount)
    }

    @Test
    fun `invoke should filter items by fuel type before grouping`() = runTest(testDispatcher) {
        val sortTypeFlow = flowOf(FuelConsumptionSortType.BY_DATE)
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val filterTypeFlow = flowOf("Diesel")
        val limitFlow = flowOf(10)

        val diesel = makeFuelConsumption("fc_1", amount = "45.5", type = "Diesel", date = 86_400_000L)
        val petrol = makeFuelConsumption("fc_2", amount = "40.0", type = "Petrol", date = 172_800_000L)

        every { repository.observe(10, NO_DATE_RANGE) } returns flowOf(listOf(diesel, petrol))

        val results = observeUseCase(
            searchQueryFlow = flowOf(NO_SEARCH_QUERY),
            dateRangeFlow = dateRangeFlow,
            filterTypeFlow = filterTypeFlow,
            sortTypeFlow = sortTypeFlow,
            limitFlow = limitFlow,
        ).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val actual = (first as Async.Success).data

        assertEquals(1, actual.size)
        assertEquals(listOf(diesel), actual.first().items)
        assertEquals(listOf(com.ivzb.urgat.data.fuelConsumption.FuelConsumptionTotal("Diesel", diesel.amount)), actual.first().total)
    }

    companion object {
        private fun makeFuelConsumption(
            id: String,
            amount: String = "45.5",
            price: String = "2.35",
            currency: String = "EUR",
            type: String = "Diesel",
            carName: String = "Mercedes w140",
            odometer: Int = 150000,
            date: Long = 1000L,
        ) = FuelConsumption(
            id = id,
            amount = BigDecimal(amount),
            price = BigDecimal(price),
            currency = currency,
            type = type,
            carName = carName,
            odometer = odometer,
            distance = 500,
            date = date,
            isDeleted = false,
            createdAt = date,
        )
    }
}

