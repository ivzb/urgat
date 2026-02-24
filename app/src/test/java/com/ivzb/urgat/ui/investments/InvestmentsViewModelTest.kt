package com.ivzb.urgat.ui.investments

import com.ivzb.urgat.data.finance.FakeFinanceRepository
import com.ivzb.urgat.R
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentAsset
import com.ivzb.urgat.data.investment.InvestmentCategory
import com.ivzb.urgat.data.investment.InvestmentNetWorth
import com.ivzb.urgat.domain.investment.FakeObserveInvestmentsUiStateUseCase
import com.ivzb.urgat.ui.components.FilterMenuItem
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
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class InvestmentsViewModelTest {

    private lateinit var fakeFinanceRepository: FakeFinanceRepository
    private lateinit var fakeObserveInvestmentsUiStateUseCase: FakeObserveInvestmentsUiStateUseCase

    private lateinit var viewModel: InvestmentsViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        fakeFinanceRepository = FakeFinanceRepository()
        fakeObserveInvestmentsUiStateUseCase = FakeObserveInvestmentsUiStateUseCase()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveInvestmentsUiStateUseCase.emit(Async.Loading)
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        val initial = viewModel.uiState.value
        assertTrue(initial.isLoading)
        assertFalse(initial.hasError)
        assertTrue(initial.investments.isEmpty())
        assertNull(initial.netWorth)
        assertTrue(initial.categories.isEmpty())
        assertTrue(initial.assets.isEmpty())

        job.cancel()
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        val investments = listOf(
            Investment(
                "1",
                "SNP 500",
                500.toBigDecimal(),
                300.toBigDecimal(),
                "etfs",
                "USD",
                1L,
                false,
                1L
            )
        )
        val fakeInvestments = listOf(
            GroupedItems(
                "2025/12",
                "1500 USD",
                investments,
            )
        )
        val fakeCategoriesMonthly = listOf(
            GroupedItems(
                group = "2025/12",
                total = "1500 USD",
                items = listOf(
                    InvestmentCategory(
                        id = "1",
                        name = "etfs",
                        total = "500 USD",
                        investments = investments,
                    )
                )
            )
        )
        val fakeNetWorth = InvestmentNetWorth(
            total = "500 USD",
        )
        val fakeCategories = listOf(
            InvestmentCategory(
                id = "1",
                name = "etfs",
                total = "500 USD",
                investments = investments,
            )
        )
        val fakeAssets = listOf(
            InvestmentAsset(
                id = "1",
                name = "SNP 500",
                category = "etfs",
                shares = 500.toBigDecimal(),
                marketValue = 500.toBigDecimal(),
                currency = "USD",
                investments = investments,
                total = "1,750.59 EUR | 2,891.05 USD",
            )
        )
        val fakeInvestmentsFiltering = listOf(
            FilterMenuItem(type = "stocks", label = "stocks", size = 1),
        )
        val fakeInvestmentsSorting = listOf(
            SortMenuItem(type = InvestmentsSortType.BY_DATE, labelResId = R.string.sort_by_date),
            SortMenuItem(type = InvestmentsSortType.BY_MARKET_VALUE, labelResId = R.string.sort_by_market_value),
        )

        val fakeInvestmentsUiState = InvestmentsUiState(
            investments = fakeInvestments,
            categoriesYearly = fakeCategoriesMonthly,
            assets = fakeAssets,
            categories = fakeCategories,
            netWorth = fakeNetWorth,
            filteringUiInfo = InvestmentsFilteringUiInfo(
                filterMenuItems = fakeInvestmentsFiltering,
                sortMenuItems = fakeInvestmentsSorting,
            )
        )
        fakeObserveInvestmentsUiStateUseCase.emit(Async.Success(fakeInvestmentsUiState))

        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(fakeInvestments, state.investments)
        assertEquals(fakeNetWorth, state.netWorth)
        assertEquals(fakeCategories, state.categories)
        assertEquals(fakeAssets, state.assets)
        assertEquals(fakeInvestmentsFiltering, state.filteringUiInfo.filterMenuItems)
        assertEquals(fakeInvestmentsSorting, state.filteringUiInfo.sortMenuItems)
        assertFalse(state.isLoading)
        assertFalse(state.hasError)
        job.cancel()
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveInvestmentsUiStateUseCase.emit(Async.Error)
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
        val investments = listOf(
            Investment(
                "1",
                "SNP 500",
                500.toBigDecimal(),
                300.toBigDecimal(),
                "etfs",
                "USD",
                1L,
                false,
                1L
            )
        )
        val fakeInvestments = listOf(
            GroupedItems(
                "2025/12",
                "1500 USD",
                investments,
            )
        )
        val fakeCategoriesMonthly = listOf(
            GroupedItems(
                group = "2025/12",
                total = "1500 USD",
                items = listOf(
                    InvestmentCategory(
                        id = "1",
                        name = "etfs",
                        total = "500 USD",
                        investments = investments,
                    )
                )
            )
        )
        val fakeNetWorth = InvestmentNetWorth(
            total = "500 USD",
        )
        val fakeCategories = listOf(
            InvestmentCategory(
                id = "1",
                name = "etfs",
                total = "500 USD",
                investments = investments,
            )
        )
        val fakeAssets = listOf(
            InvestmentAsset(
                id = "1",
                name = "SNP 500",
                category = "etfs",
                shares = 500.toBigDecimal(),
                marketValue = 500.toBigDecimal(),
                currency = "USD",
                investments = investments,
                total = "1,750.59 EUR | 2,891.05 USD",
            )
        )
        val fakeInvestmentsFiltering = listOf(
            FilterMenuItem(type = "stocks", label = "stocks", size = 1),
        )
        val fakeInvestmentsSorting = listOf(
            SortMenuItem(type = InvestmentsSortType.BY_DATE, labelResId = R.string.sort_by_date),
            SortMenuItem(type = InvestmentsSortType.BY_MARKET_VALUE, labelResId = R.string.sort_by_market_value),
        )

        val fakeInvestmentsUiState = InvestmentsUiState(
            investments = fakeInvestments,
            categoriesYearly = fakeCategoriesMonthly,
            assets = fakeAssets,
            categories = fakeCategories,
            netWorth = fakeNetWorth,
            filteringUiInfo = InvestmentsFilteringUiInfo(
                filterMenuItems = fakeInvestmentsFiltering,
                sortMenuItems = fakeInvestmentsSorting,
            )
        )
        fakeObserveInvestmentsUiStateUseCase.emit(Async.Success(fakeInvestmentsUiState))

        viewModel = createViewModel()

        viewModel.onIntent(InvestmentsIntent.SearchQueryChanged("snp"))

        advanceUntilIdle()

        assertEquals("snp", fakeObserveInvestmentsUiStateUseCase.lastQuery)
    }

    @Test
    fun `setFiltering updates finance repository`() = runTest {
        viewModel = createViewModel()

        val expected = "etfs"

        viewModel.onIntent(InvestmentsIntent.SetFiltering(expected))
        advanceUntilIdle()

        assertEquals(expected, fakeFinanceRepository.investmentsFilterTypeFlow.value)
    }

    @Test
    fun `setSorting updates finance repository`() = runTest {
        viewModel = createViewModel()

        val expected = InvestmentsSortType.BY_MARKET_VALUE

        viewModel.onIntent(InvestmentsIntent.SetSorting(expected))
        advanceUntilIdle()

        assertEquals(expected, fakeFinanceRepository.investmentsSortTypeFlow.value)
    }

    private fun createViewModel() = InvestmentsViewModel(
        fakeObserveInvestmentsUiStateUseCase,
        fakeFinanceRepository,
    )
}