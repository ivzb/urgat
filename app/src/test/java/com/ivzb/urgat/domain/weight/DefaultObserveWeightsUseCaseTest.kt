package com.ivzb.urgat.domain.weight

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.weight.Weight
import com.ivzb.urgat.data.weight.WeightsRepository
import com.ivzb.urgat.ui.weights.WeightsSortType
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
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveWeightsUseCaseTest {

    private lateinit var repository: WeightsRepository
    private lateinit var observeUseCase: ObserveWeightsUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
        observeUseCase = DefaultObserveWeightsUseCase(repository)
    }

    @Test
    fun `invoke should return Success with filtered items sorted by date`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("weight")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val sortTypeFlow = flowOf(WeightsSortType.BY_DATE)
        val limitFlow = flowOf(limit)
        val repositoryStream = flowOf(
            listOf(
                Weight("1", 79.5.toBigDecimal(), 100_000_000, false, createdAt = 100_000_000),
                Weight("2", 81.3.toBigDecimal(), 200_000_000, false, createdAt = 200_000_000),
                Weight("3", 80.7.toBigDecimal(), 300_000_000, false, createdAt = 300_000_000),
            )
        )

        every { repository.observe(limit, NO_DATE_RANGE, "weight") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        val expected = listOf(
            Weight("3", 80.7.toBigDecimal(), 300_000_000, false, createdAt = 300_000_000),
            Weight("2", 81.3.toBigDecimal(), 200_000_000, false, createdAt = 200_000_000),
            Weight("1", 79.5.toBigDecimal(), 100_000_000, false, createdAt = 100_000_000),
        )

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val actual = (first as Async.Success).data
        assertEquals(expected, actual)
    }

    @Test
    fun `invoke should return Success with filtered items sorted by min`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("weight")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val sortTypeFlow = flowOf(WeightsSortType.BY_MIN)
        val limitFlow = flowOf(limit)
        val repositoryStream = flowOf(
            listOf(
                Weight("1", 79.5.toBigDecimal(), 100_000_000, false, createdAt = 100_000_000),
                Weight("2", 81.3.toBigDecimal(), 200_000_000, false, createdAt = 200_000_000),
                Weight("3", 80.7.toBigDecimal(), 300_000_000, false, createdAt = 300_000_000),
            )
        )

        every { repository.observe(limit, NO_DATE_RANGE, "weight") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        val expected = listOf(
            Weight("1", 79.5.toBigDecimal(), 100_000_000, false, createdAt = 100_000_000),
            Weight("3", 80.7.toBigDecimal(), 300_000_000, false, createdAt = 300_000_000),
            Weight("2", 81.3.toBigDecimal(), 200_000_000, false, createdAt = 200_000_000),
        )

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val actual = (first as Async.Success).data
        assertEquals(expected, actual)
    }

    @Test
    fun `invoke should return Success with filtered items sorted by max`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("weight")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val sortTypeFlow = flowOf(WeightsSortType.BY_MAX)
        val limitFlow = flowOf(limit)
        val repositoryStream = flowOf(
            listOf(
                Weight("1", 79.5.toBigDecimal(), 100_000_000, false, createdAt = 100_000_000),
                Weight("2", 81.3.toBigDecimal(), 200_000_000, false, createdAt = 200_000_000),
                Weight("3", 80.7.toBigDecimal(), 300_000_000, false, createdAt = 300_000_000),
            )
        )

        every { repository.observe(limit, NO_DATE_RANGE, "weight") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        val expected = listOf(
            Weight("2", 81.3.toBigDecimal(), 200_000_000, false, createdAt = 200_000_000),
            Weight("3", 80.7.toBigDecimal(), 300_000_000, false, createdAt = 300_000_000),
            Weight("1", 79.5.toBigDecimal(), 100_000_000, false, createdAt = 100_000_000),
        )

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val actual = (first as Async.Success).data
        assertEquals(expected, actual)
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
}
