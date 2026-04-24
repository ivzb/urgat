package com.ivzb.urgat.domain.trades

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.trade.Trade
import com.ivzb.urgat.data.trade.TradesRepository
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.ui.trades.TradesSortType
import com.ivzb.urgat.util.timestamp
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
class DefaultObserveGroupedTradesMonthlyUseCaseTest {

    private lateinit var repository: TradesRepository
    private lateinit var observeUseCase: ObserveGroupedTradesMonthlyUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
        every { repository.isRefreshingFlow } returns MutableStateFlow(false)
        observeUseCase = DefaultObserveGroupedTradesMonthlyUseCase(repository)
    }

    @Test
    fun `invoke should return Success with items grouped by month sorted by date`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("AAPL")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val sortTypeFlow = flowOf(TradesSortType.BY_DATE)
        val limitFlow = flowOf(limit)
        val repositoryStream = flowOf(
            listOf(
                Trade("1", "AAPL", BigDecimal("10"), BigDecimal("150"), BigDecimal("1.5"), "USD", "buy", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25)),
                Trade("2", "AAPL", BigDecimal("5"), BigDecimal("155"), BigDecimal("1.5"), "USD", "buy", timestamp(2025, 11, 27), false, timestamp(2025, 11, 27)),
                Trade("3", "AAPL", BigDecimal("8"), BigDecimal("160"), BigDecimal("2.0"), "USD", "sell", timestamp(2025, 12, 9), false, timestamp(2025, 12, 9)),
                Trade("4", "AAPL", BigDecimal("3"), BigDecimal("170"), BigDecimal("1.0"), "USD", "buy", timestamp(2025, 12, 17), false, timestamp(2025, 12, 17)),
            ),
        )

        every { repository.observe(limit, NO_DATE_RANGE, "AAPL") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val actual = (first as Async.Success).data

        assertEquals(2, actual.size)
        assertEquals("December 2025", actual[0].group)
        assertEquals("November 2025", actual[1].group)
        assertEquals(2, actual[0].items.size)
        assertEquals(2, actual[1].items.size)
        // Sorted by date desc within each group
        assertEquals("4", actual[0].items[0].id)
        assertEquals("3", actual[0].items[1].id)
        assertEquals("2", actual[1].items[0].id)
        assertEquals("1", actual[1].items[1].id)
    }

    @Test
    fun `invoke should return Success with items sorted by cost`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val sortTypeFlow = flowOf(TradesSortType.BY_COST)
        val limitFlow = flowOf(limit)
        val repositoryStream = flowOf(
            listOf(
                Trade("1", "AAPL", BigDecimal("10"), BigDecimal("150"), BigDecimal("0"), "USD", "buy", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25)),
                Trade("2", "GOOG", BigDecimal("5"), BigDecimal("200"), BigDecimal("0"), "USD", "buy", timestamp(2025, 11, 27), false, timestamp(2025, 11, 27)),
            ),
        )

        every { repository.observe(limit, NO_DATE_RANGE) } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val actual = (first as Async.Success).data

        assertEquals(1, actual.size)
        // AAPL totalPrice = 10*150+0 = 1500, GOOG totalPrice = 5*200+0 = 1000
        // Sorted by cost desc: AAPL first
        assertEquals("1", actual[0].items[0].id)
        assertEquals("2", actual[0].items[1].id)
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

    @Test
    fun `isRefreshingFlow starts as false`() = runTest(testDispatcher) {
        assertEquals(false, observeUseCase.isRefreshingFlow.value)
    }

    @Test
    fun `isRefreshingFlow delegates from repository`() = runTest(testDispatcher) {
        val repositoryRefreshingFlow = MutableStateFlow(false)
        every { repository.isRefreshingFlow } returns repositoryRefreshingFlow

        val useCase = DefaultObserveGroupedTradesMonthlyUseCase(repository)

        assertEquals(false, useCase.isRefreshingFlow.value)

        repositoryRefreshingFlow.value = true
        assertEquals(true, useCase.isRefreshingFlow.value)

        repositoryRefreshingFlow.value = false
        assertEquals(false, useCase.isRefreshingFlow.value)
    }

    @Test
    fun `invoke with filter type filters by orderType`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val filterTypeFlow = flowOf("buy")
        val sortTypeFlow = flowOf(TradesSortType.BY_DATE)
        val limitFlow = flowOf(limit)
        val repositoryStream = flowOf(
            listOf(
                Trade("1", "AAPL", BigDecimal("10"), BigDecimal("150"), BigDecimal("0"), "USD", "buy", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25)),
                Trade("2", "AAPL", BigDecimal("5"), BigDecimal("155"), BigDecimal("0"), "USD", "sell", timestamp(2025, 11, 27), false, timestamp(2025, 11, 27)),
                Trade("3", "GOOG", BigDecimal("8"), BigDecimal("160"), BigDecimal("0"), "USD", "buy", timestamp(2025, 11, 28), false, timestamp(2025, 11, 28)),
            ),
        )

        every { repository.observe(limit, NO_DATE_RANGE) } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val actual = (first as Async.Success).data

        assertEquals(1, actual.size)
        assertEquals(2, actual[0].items.size)
        // Only buy trades
        assertEquals("buy", actual[0].items[0].orderType)
        assertEquals("buy", actual[0].items[1].orderType)
    }

}


