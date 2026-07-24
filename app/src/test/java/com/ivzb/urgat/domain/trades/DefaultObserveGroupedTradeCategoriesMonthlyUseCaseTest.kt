package com.ivzb.urgat.domain.trades

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.random.RandomRepository
import com.ivzb.urgat.data.trade.Trade
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
class DefaultObserveGroupedTradeCategoriesMonthlyUseCaseTest {

    private lateinit var repository: TradesRepository
    private lateinit var randomRepository: RandomRepository
    private lateinit var observeUseCase: ObserveGroupedTradeCategoriesMonthlyUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
        randomRepository = mockk()
        every { randomRepository.uuid() } returns "id"
        observeUseCase = DefaultObserveGroupedTradeCategoriesMonthlyUseCase(repository, randomRepository)
    }

    @Test
    fun `invoke should group by month then by name sorted by total desc`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val sortTypeFlow = flowOf(TradesSortType.BY_DATE)
        val limitFlow = flowOf(limit)
        val repositoryStream = flowOf(
            listOf(
                Trade("1", "AAPL", BigDecimal("150"), "USD", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25)),
                Trade("2", "AAPL", BigDecimal("155"), "USD", timestamp(2025, 11, 27), false, timestamp(2025, 11, 27)),
                Trade("3", "TSLA", BigDecimal("200"), "USD", timestamp(2025, 11, 20), false, timestamp(2025, 11, 20)),

                Trade("4", "AAPL", BigDecimal("160"), "USD", timestamp(2025, 12, 9), false, timestamp(2025, 12, 9)),
            ),
        )

        every { repository.observe(limit, NO_DATE_RANGE) } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val actual = (first as Async.Success).data

        // grouped by month, sorted descending
        assertEquals(2, actual.size)
        assertEquals("December 2025", actual[0].group)
        assertEquals("November 2025", actual[1].group)

        // December: single AAPL category
        assertEquals(listOf(TradeTotal("USD", "160".toBigDecimal())), actual[0].total)
        assertEquals(1, actual[0].items.size)
        assertEquals("AAPL", actual[0].items[0].name)
        assertEquals(listOf(TradeTotal("USD", "160".toBigDecimal())), actual[0].items[0].totals)

        // November: AAPL (305) sorted before TSLA (200)
        assertEquals(listOf(TradeTotal("USD", "505".toBigDecimal())), actual[1].total)
        assertEquals(2, actual[1].items.size)
        assertEquals("AAPL", actual[1].items[0].name)
        assertEquals(listOf(TradeTotal("USD", "305".toBigDecimal())), actual[1].items[0].totals)
        assertEquals(2, actual[1].items[0].trades.size)
        assertEquals("TSLA", actual[1].items[1].name)
        assertEquals(listOf(TradeTotal("USD", "200".toBigDecimal())), actual[1].items[1].totals)
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


