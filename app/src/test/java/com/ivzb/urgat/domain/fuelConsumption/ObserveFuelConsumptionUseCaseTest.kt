package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
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
class ObserveFuelConsumptionUseCaseTest {

    private lateinit var repository: FuelConsumptionRepository
    private lateinit var observeUseCase: ObserveFuelConsumptionUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
        observeUseCase = DefaultObserveFuelConsumptionUseCase(repository)
    }

    @Test
    fun `invoke should return Success with items sorted by date`() = runTest(testDispatcher) {
        `invoke should return Success with items sorted by`(
            sortType = FuelConsumptionSortType.BY_DATE,
            expected = listOf(
                makeFuelConsumption("fc_1", price = "2.35", date = 3000L),
                makeFuelConsumption("fc_3", price = "2.30", date = 2000L),
                makeFuelConsumption("fc_2", price = "2.40", date = 1000L),
            ),
        )
    }

    @Test
    fun `invoke should return Success with items sorted by min price`() = runTest(testDispatcher) {
        `invoke should return Success with items sorted by`(
            sortType = FuelConsumptionSortType.BY_MIN,
            expected = listOf(
                makeFuelConsumption("fc_3", price = "2.30", date = 2000L),
                makeFuelConsumption("fc_1", price = "2.35", date = 3000L),
                makeFuelConsumption("fc_2", price = "2.40", date = 1000L),
            ),
        )
    }

    @Test
    fun `invoke should return Success with items sorted by max price`() = runTest(testDispatcher) {
        `invoke should return Success with items sorted by`(
            sortType = FuelConsumptionSortType.BY_MAX,
            expected = listOf(
                makeFuelConsumption("fc_2", price = "2.40", date = 1000L),
                makeFuelConsumption("fc_1", price = "2.35", date = 3000L),
                makeFuelConsumption("fc_3", price = "2.30", date = 2000L),
            ),
        )
    }

    private fun `invoke should return Success with items sorted by`(
        sortType: FuelConsumptionSortType,
        expected: List<FuelConsumption>,
    ) = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("Mercedes")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val sortTypeFlow = flowOf(sortType)
        val limit = 10
        val limitFlow = flowOf(limit)

        val repositoryStream = flowOf(
            listOf(
                makeFuelConsumption("fc_2", price = "2.40", date = 1000L),
                makeFuelConsumption("fc_1", price = "2.35", date = 3000L),
                makeFuelConsumption("fc_3", price = "2.30", date = 2000L),
            )
        )

        every { repository.observe(limit, NO_DATE_RANGE, "Mercedes") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val actual = (first as Async.Success).data
        assertEquals(expected, actual)
    }

    @Test
    fun `invoke should return Error when repository throws`() = runTest(testDispatcher) {
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val limitFlow = flowOf(limit)

        every { repository.observe(limit, NO_DATE_RANGE) } throws RuntimeException("Test error")

        val results = observeUseCase(
            searchQueryFlow = null,
            dateRangeFlow = dateRangeFlow,
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = limitFlow,
        ).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Error)
    }

    @Test
    fun `invoke with search query should use search observe`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("Mercedes")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val limitFlow = flowOf(limit)

        val fc = makeFuelConsumption("fc_1", carName = "Mercedes w140")
        every { repository.observe(limit, NO_DATE_RANGE, "Mercedes") } returns flowOf(listOf(fc))

        val results = observeUseCase(
            searchQueryFlow = searchQueryFlow,
            dateRangeFlow = dateRangeFlow,
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = limitFlow,
        ).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val actual = (first as Async.Success).data
        assertEquals(1, actual.size)
        assertEquals("Mercedes w140", actual[0].carName)
    }

    @Test
    fun `invoke with empty search query should use non-search observe`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val limitFlow = flowOf(limit)

        val fc1 = makeFuelConsumption("fc_1")
        val fc2 = makeFuelConsumption("fc_2")
        every { repository.observe(limit, NO_DATE_RANGE) } returns flowOf(listOf(fc1, fc2))

        val results = observeUseCase(
            searchQueryFlow = searchQueryFlow,
            dateRangeFlow = dateRangeFlow,
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = limitFlow,
        ).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val actual = (first as Async.Success).data
        assertEquals(2, actual.size)
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
            date = date,
            isDeleted = false,
            createdAt = date,
        )
    }
}
