package com.ivzb.urgat.domain.trades

import com.ivzb.urgat.data.trade.Trade
import com.ivzb.urgat.data.trade.TradesRepository
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.R
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_ITEMS_LIMIT
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.ui.trades.TradesFilterType_All
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
class DefaultObserveTradesFilteringUseCaseTest {

    private lateinit var repository: TradesRepository
    private lateinit var observeUseCase: ObserveTradesFilteringUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
        observeUseCase = DefaultObserveTradesFilteringUseCase(repository)
    }

    @Test
    fun `invoke should return Success with all filter item`() = runTest(testDispatcher) {
        val expected = listOf(
            FilterMenuItem(type = TradesFilterType_All, labelResId = R.string.filter_all, size = 5),
        )

        val repositoryStream = flowOf(
            listOf(
                Trade("1", "AAPL", BigDecimal("100"), "USD", 1L, false, 1L),
                Trade("2", "GOOG", BigDecimal("100"), "USD", 1L, false, 1L),
                Trade("3", "TSLA", BigDecimal("100"), "USD", 1L, false, 1L),
                Trade("4", "MSFT", BigDecimal("100"), "USD", 1L, false, 1L),
                Trade("5", "AMZN", BigDecimal("100"), "USD", 1L, false, 1L),
            )
        )

        every { repository.observe(NO_ITEMS_LIMIT, NO_DATE_RANGE) } returns repositoryStream

        val results = observeUseCase().toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val items = (first as Async.Success).data
        assertEquals(expected, items)
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
        val first = results.first()
        assert(first is Async.Error)
    }
}

