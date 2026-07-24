package com.ivzb.urgat.domain.trades

import com.ivzb.urgat.R
import com.ivzb.urgat.data.trade.Trade
import com.ivzb.urgat.data.trade.TradeCategory
import com.ivzb.urgat.data.trade.TradeTotal
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.ui.trades.TradesSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveTradesUiStateUseCaseTest {

    private lateinit var fakeObserveGroupedTradesMonthlyUseCase: FakeObserveGroupedTradesMonthlyUseCase
    private lateinit var fakeObserveGroupedTradeCategoriesMonthlyUseCase: FakeObserveGroupedTradeCategoriesMonthlyUseCase
    private lateinit var fakeObserveGroupedTradeCategoriesYearlyUseCase: FakeObserveGroupedTradeCategoriesYearlyUseCase
    private lateinit var fakeObserveGroupedTradeCategoriesAllTimeUseCase: FakeObserveGroupedTradeCategoriesAllTimeUseCase
    private lateinit var fakeObserveTradesSortingUseCase: FakeObserveTradesSortingUseCase

    private lateinit var useCase: ObserveTradesUiStateUseCase

    @Before
    fun setUp() {
        fakeObserveGroupedTradesMonthlyUseCase = FakeObserveGroupedTradesMonthlyUseCase()
        fakeObserveGroupedTradeCategoriesMonthlyUseCase = FakeObserveGroupedTradeCategoriesMonthlyUseCase()
        fakeObserveGroupedTradeCategoriesYearlyUseCase = FakeObserveGroupedTradeCategoriesYearlyUseCase()
        fakeObserveGroupedTradeCategoriesAllTimeUseCase = FakeObserveGroupedTradeCategoriesAllTimeUseCase()
        fakeObserveTradesSortingUseCase = FakeObserveTradesSortingUseCase()

        useCase = DefaultObserveTradesUiStateUseCase(
            fakeObserveGroupedTradesMonthlyUseCase,
            fakeObserveGroupedTradeCategoriesMonthlyUseCase,
            fakeObserveGroupedTradeCategoriesYearlyUseCase,
            fakeObserveGroupedTradeCategoriesAllTimeUseCase,
            fakeObserveTradesSortingUseCase,
        )
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveGroupedTradesMonthlyUseCase.emit(Async.Loading)
        fakeObserveGroupedTradeCategoriesMonthlyUseCase.emit(Async.Loading)
        fakeObserveGroupedTradeCategoriesYearlyUseCase.emit(Async.Loading)
        fakeObserveGroupedTradeCategoriesAllTimeUseCase.emit(Async.Loading)
        fakeObserveTradesSortingUseCase.emit(Async.Loading)

        val searchQueryFlow = flowOf("")
        val sortTypeFlow = flowOf(TradesSortType.BY_DATE)

        val results = useCase(searchQueryFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.isLoading)
        assertFalse(uiState.hasError)
        assertTrue(uiState.trades.isEmpty())
        assertTrue(uiState.categoriesMonthly.isEmpty())
        assertTrue(uiState.categoriesYearly.isEmpty())
        assertTrue(uiState.categoriesAllTime.isEmpty())
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val sortTypeFlow = flowOf(TradesSortType.BY_DATE)

        val results = useCase(searchQueryFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertEquals(fakeTrades, uiState.trades)
        assertEquals(fakeCategoriesMonthly, uiState.categoriesMonthly)
        assertEquals(fakeCategoriesYearly, uiState.categoriesYearly)
        assertEquals(fakeCategoriesAllTime, uiState.categoriesAllTime)
        assertEquals(fakeTradesSorting, uiState.filteringUiInfo.sortMenuItems)
        assertFalse(uiState.filteringUiInfo.showSortBadge)
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveGroupedTradesMonthlyUseCase.emit(Async.Error)
        fakeObserveGroupedTradeCategoriesMonthlyUseCase.emit(Async.Error)
        fakeObserveGroupedTradeCategoriesYearlyUseCase.emit(Async.Error)
        fakeObserveGroupedTradeCategoriesAllTimeUseCase.emit(Async.Error)
        fakeObserveTradesSortingUseCase.emit(Async.Error)

        val searchQueryFlow = flowOf("")
        val sortTypeFlow = flowOf(TradesSortType.BY_DATE)

        val results = useCase(searchQueryFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.hasError)
        assertFalse(uiState.isLoading)
    }

    @Test
    fun `hasAppliedFilters is false when no filters are applied`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val sortTypeFlow = flowOf(TradesSortType.BY_DATE)

        val results = useCase(searchQueryFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertFalse(uiState.filteringUiInfo.hasAppliedFilters)
        assertEquals("", uiState.filteringUiInfo.searchQuery)
        assertFalse(uiState.filteringUiInfo.showFilterBadge)
        assertFalse(uiState.filteringUiInfo.showSortBadge)
        assertEquals(R.string.no_trades, uiState.filteringUiInfo.emptyLabel)
        assertEquals(R.string.no_trades_message, uiState.filteringUiInfo.emptyMessage)
        assertEquals(R.string.add_trade, uiState.filteringUiInfo.emptyButtonLabel)
    }

    @Test
    fun `hasAppliedFilters is true when search query is not empty`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("AAPL")
        val sortTypeFlow = flowOf(TradesSortType.BY_DATE)

        val results = useCase(searchQueryFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.filteringUiInfo.hasAppliedFilters)
        assertEquals("AAPL", uiState.filteringUiInfo.searchQuery)
        assertEquals(R.string.no_trades_search, uiState.filteringUiInfo.emptyLabel)
        assertEquals(R.string.no_trades_search_message, uiState.filteringUiInfo.emptyMessage)
        assertEquals(R.string.reset_filters, uiState.filteringUiInfo.emptyButtonLabel)
    }

    private fun emitSuccessData() {
        fakeObserveGroupedTradesMonthlyUseCase.emit(Async.Success(fakeTrades))
        fakeObserveGroupedTradeCategoriesMonthlyUseCase.emit(Async.Success(fakeCategoriesMonthly))
        fakeObserveGroupedTradeCategoriesYearlyUseCase.emit(Async.Success(fakeCategoriesYearly))
        fakeObserveGroupedTradeCategoriesAllTimeUseCase.emit(Async.Success(fakeCategoriesAllTime))
        fakeObserveTradesSortingUseCase.emit(Async.Success(fakeTradesSorting))
    }

    companion object {

        private val trades = listOf(Trade("1", "AAPL", BigDecimal.TEN, "USD", 1L, false, 1L))

        private val fakeTrades = listOf(
            GroupedItems(group = "2025/12", total = listOf(TradeTotal("USD", "10".toBigDecimal())), items = trades)
        )

        private val fakeCategoriesMonthly = listOf(
            GroupedItems(
                group = "2025/12", total = listOf(TradeTotal("USD", "10".toBigDecimal())),
                items = listOf(TradeCategory(id = "1", name = "AAPL", totals = listOf(TradeTotal("USD", "10".toBigDecimal())), trades = trades))
            )
        )

        private val fakeCategoriesYearly = listOf(
            GroupedItems(
                group = "2025", total = listOf(TradeTotal("USD", "10".toBigDecimal())),
                items = listOf(TradeCategory(id = "1", name = "AAPL", totals = listOf(TradeTotal("USD", "10".toBigDecimal())), trades = trades))
            )
        )

        private val fakeCategoriesAllTime = listOf(
            TradeCategory(id = "1", name = "AAPL", totals = listOf(TradeTotal("USD", "10".toBigDecimal())), trades = trades)
        )

        private val fakeTradesSorting = listOf(
            SortMenuItem(type = TradesSortType.BY_DATE, labelResId = R.string.sort_by_date),
        )

    }

}

