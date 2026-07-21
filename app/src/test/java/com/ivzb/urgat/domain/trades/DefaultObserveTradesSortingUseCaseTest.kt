package com.ivzb.urgat.domain.trades

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.ui.trades.TradesSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultObserveTradesSortingUseCaseTest {

    private lateinit var observeUseCase: ObserveTradesSortingUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        observeUseCase = DefaultObserveTradesSortingUseCase()
    }

    @Test
    fun `invoke should return Success with sort menu items`() = runTest(testDispatcher) {
        val expected = listOf(
            SortMenuItem(type = TradesSortType.BY_DATE, labelResId = R.string.sort_by_date),
        )

        val results = observeUseCase().toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val items = (first as Async.Success).data
        assertEquals(expected, items)
    }

}

