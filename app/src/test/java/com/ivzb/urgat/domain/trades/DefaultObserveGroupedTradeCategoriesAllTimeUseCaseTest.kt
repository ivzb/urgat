package com.ivzb.urgat.domain.trades

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.random.RandomRepository
import com.ivzb.urgat.data.trade.Trade
import com.ivzb.urgat.data.trade.TradeCategory
import com.ivzb.urgat.data.trade.TradeTotal
import com.ivzb.urgat.data.trade.TradesRepository
import com.ivzb.urgat.ui.trades.TradesSortType
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
class DefaultObserveGroupedTradeCategoriesAllTimeUseCaseTest {

    private lateinit var repository: TradesRepository
    private lateinit var randomRepository: RandomRepository
    private lateinit var observeUseCase: ObserveGroupedTradeCategoriesAllTimeUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
        randomRepository = mockk()
        every { randomRepository.uuid() } returns TEST_UUID
        observeUseCase = DefaultObserveGroupedTradeCategoriesAllTimeUseCase(repository, randomRepository)
    }

    @Test
    fun `invoke should group by name sorted by total desc`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("AAPL")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val sortTypeFlow = flowOf(TradesSortType.BY_DATE)
        val limitFlow = flowOf(limit)
        val repositoryStream = flowOf(trades)

        every { repository.observe(limit, NO_DATE_RANGE, "AAPL") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        val expected = listOf(
            TradeCategory(id = TEST_UUID, name = "AAPL", totals = listOf(TradeTotal("USD", "480".toBigDecimal())), trades = listOf(trades[3], trades[2], trades[0])),
            TradeCategory(id = TEST_UUID, name = "TSLA", totals = listOf(TradeTotal("USD", "450".toBigDecimal())), trades = listOf(trades[4], trades[1])),
        )

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val actual = (first as Async.Success).data
        assertEquals(expected, actual)
    }

    @Test
    fun `invoke should group by name when no sort type is applied`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("AAPL")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val limitFlow = flowOf(limit)
        val repositoryStream = flowOf(trades)

        every { repository.observe(limit, NO_DATE_RANGE, "AAPL") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, null, limitFlow).toList()

        advanceUntilIdle()

        val expected = listOf(
            TradeCategory(id = TEST_UUID, name = "AAPL", totals = listOf(TradeTotal("USD", "480".toBigDecimal())), trades = listOf(trades[0], trades[2], trades[3])),
            TradeCategory(id = TEST_UUID, name = "TSLA", totals = listOf(TradeTotal("USD", "450".toBigDecimal())), trades = listOf(trades[1], trades[4])),
        )

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val actual = (first as Async.Success).data
        assertEquals(expected, actual)
    }

    @Test
    fun `invoke should return Success with totals per currency`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val limitFlow = flowOf(limit)
        val multiCurrencyTrades = listOf(
            Trade("1", "AAPL", BigDecimal("150"), "USD", timestamp(2024, 3, 9), false, timestamp(2024, 3, 9)),
            Trade("2", "AAPL", BigDecimal("300"), "EUR", timestamp(2025, 5, 9), false, timestamp(2025, 5, 9)),
            Trade("3", "AAPL", BigDecimal("170"), "usd", timestamp(2025, 7, 9), false, timestamp(2025, 7, 9)),
        )

        every { repository.observe(limit, NO_DATE_RANGE) } returns flowOf(multiCurrencyTrades)

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, null, limitFlow).toList()

        advanceUntilIdle()

        val expected = listOf(
            TradeCategory(
                id = TEST_UUID,
                name = "AAPL",
                totals = listOf(
                    TradeTotal("EUR", "300".toBigDecimal()),
                    TradeTotal("USD", "320".toBigDecimal()),
                ),
                trades = multiCurrencyTrades,
            ),
        )

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val actual = (first as Async.Success).data
        assertEquals(expected, actual)
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
        val actual = (first as Async.Success).data
        assertEquals(listOf(), actual)
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

        private const val TEST_UUID = "mock_id"

        private val trades = listOf(
            Trade("1", "AAPL", BigDecimal("150"), "USD", timestamp(2024, 11, 25), false, timestamp(2024, 11, 25)),
            Trade("2", "TSLA", BigDecimal("200"), "USD", timestamp(2024, 12, 27), false, timestamp(2024, 12, 27)),

            Trade("3", "AAPL", BigDecimal("160"), "USD", timestamp(2025, 3, 9), false, timestamp(2025, 3, 9)),
            Trade("4", "AAPL", BigDecimal("170"), "USD", timestamp(2025, 12, 17), false, timestamp(2025, 12, 17)),
            Trade("5", "TSLA", BigDecimal("250"), "USD", timestamp(2025, 12, 20), false, timestamp(2025, 12, 20)),
        )

    }

}

