package com.ivzb.urgat.ui.trades

import com.ivzb.urgat.R
import com.ivzb.urgat.data.finance.FakeFinanceRepository
import com.ivzb.urgat.data.trade.Trade
import com.ivzb.urgat.data.trade.TradeCategory
import com.ivzb.urgat.data.trade.TradeTotal
import com.ivzb.urgat.domain.trades.FakeObserveTradesUiStateUseCase
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TradesViewModelTest {

    private lateinit var fakeFinanceRepository: FakeFinanceRepository
    private lateinit var fakeObserveTradesUiStateUseCase: FakeObserveTradesUiStateUseCase

    private lateinit var viewModel: TradesViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeFinanceRepository = FakeFinanceRepository()
        fakeObserveTradesUiStateUseCase = FakeObserveTradesUiStateUseCase()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveTradesUiStateUseCase.emit(Async.Loading)
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        val initial = viewModel.uiState.value
        assertTrue(initial.isLoading)
        assertFalse(initial.hasError)
        assertTrue(initial.trades.isEmpty())
        assertTrue(initial.categoriesMonthly.isEmpty())
        assertTrue(initial.categoriesYearly.isEmpty())
        assertTrue(initial.categoriesAllTime.isEmpty())

        job.cancel()
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        fakeObserveTradesUiStateUseCase.emit(Async.Success(fakeTradesUiState))

        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(fakeTrades, state.trades)
        assertEquals(fakeCategoriesMonthly, state.categoriesMonthly)
        assertEquals(fakeCategoriesYearly, state.categoriesYearly)
        assertEquals(fakeCategoriesAllTime, state.categoriesAllTime)
        assertEquals(fakeTradesSorting, state.filteringUiInfo.sortMenuItems)
        assertFalse(state.isLoading)
        assertFalse(state.hasError)

        job.cancel()
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveTradesUiStateUseCase.emit(Async.Error)
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.hasError)
        assertFalse(state.isLoading)

        job.cancel()
    }

    @Test
    fun `onSearchQueryChanged updates query and triggers new emission`() = runTest {
        fakeObserveTradesUiStateUseCase.emit(Async.Success(fakeTradesUiState))

        viewModel = createViewModel()

        viewModel.onIntent(TradesIntent.SearchQueryChanged("AAPL"))

        advanceUntilIdle()

        assertEquals("AAPL", fakeObserveTradesUiStateUseCase.lastQuery)
    }

    @Test
    fun `setSorting updates finance repository`() = runTest {
        viewModel = createViewModel()

        viewModel.onIntent(TradesIntent.SetSorting(TradesSortType.BY_DATE))
        advanceUntilIdle()

        assertEquals(TradesSortType.BY_DATE, fakeFinanceRepository.tradesSortTypeFlow.value)
    }

    @Test
    fun `resetFilters resets search query and sort type to defaults`() = runTest {
        viewModel = createViewModel()

        viewModel.onIntent(TradesIntent.SearchQueryChanged("AAPL"))
        advanceUntilIdle()

        assertEquals("AAPL", fakeFinanceRepository.searchQueryFlow.value)

        viewModel.onIntent(TradesIntent.ResetFilters)
        advanceUntilIdle()

        assertEquals("", fakeFinanceRepository.searchQueryFlow.value)
        assertEquals(TradesSortType.BY_DATE, fakeFinanceRepository.tradesSortTypeFlow.value)
    }

    private fun createViewModel() = TradesViewModel(
        fakeObserveTradesUiStateUseCase,
        fakeFinanceRepository,
    )

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

        private val fakeTradesUiState = TradesUiState(
            trades = fakeTrades,
            categoriesMonthly = fakeCategoriesMonthly,
            categoriesYearly = fakeCategoriesYearly,
            categoriesAllTime = fakeCategoriesAllTime,
            filteringUiInfo = TradesFilteringUiInfo(
                sortMenuItems = fakeTradesSorting,
            ),
        )

    }
}

