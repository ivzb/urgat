package com.ivzb.urgat.ui.investments

import androidx.lifecycle.SavedStateHandle
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentAsset
import com.ivzb.urgat.data.investment.InvestmentCategory
import com.ivzb.urgat.data.investment.InvestmentNetWorth
import com.ivzb.urgat.domain.investment.FakeObserveGroupedInvestmentsUseCase
import com.ivzb.urgat.domain.investment.FakeObserveInvestmentAssetsUseCase
import com.ivzb.urgat.domain.investment.FakeObserveInvestmentCategoriesUseCase
import com.ivzb.urgat.domain.investment.FakeObserveInvestmentNetWorthUseCase
import com.ivzb.urgat.domain.investment.FakeObserveInvestmentsFilteringUseCase
import com.ivzb.urgat.ui.GroupedItems
import com.ivzb.urgat.ui.components.FilterMenuItem
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

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var fakeObserveGroupedInvestmentsUseCase: FakeObserveGroupedInvestmentsUseCase
    private lateinit var fakeObserveInvestmentNetWorthUseCase: FakeObserveInvestmentNetWorthUseCase
    private lateinit var fakeObserveInvestmentCategoriesUseCase: FakeObserveInvestmentCategoriesUseCase
    private lateinit var fakeObserveInvestmentAssetsUseCase: FakeObserveInvestmentAssetsUseCase
    private lateinit var fakeObserveInvestmentsFilteringUseCase: FakeObserveInvestmentsFilteringUseCase

    private lateinit var viewModel: InvestmentsViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        savedStateHandle = SavedStateHandle(mapOf(INVESTMENTS_FILTER_SAVED_STATE_KEY to null))
        fakeObserveGroupedInvestmentsUseCase = FakeObserveGroupedInvestmentsUseCase()
        fakeObserveInvestmentNetWorthUseCase = FakeObserveInvestmentNetWorthUseCase()
        fakeObserveInvestmentCategoriesUseCase = FakeObserveInvestmentCategoriesUseCase()
        fakeObserveInvestmentAssetsUseCase = FakeObserveInvestmentAssetsUseCase()
        fakeObserveInvestmentsFilteringUseCase = FakeObserveInvestmentsFilteringUseCase()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveGroupedInvestmentsUseCase.emit(Async.Loading)
        fakeObserveInvestmentNetWorthUseCase.emit(Async.Loading)
        fakeObserveInvestmentCategoriesUseCase.emit(Async.Loading)
        fakeObserveInvestmentAssetsUseCase.emit(Async.Loading)
        fakeObserveInvestmentsFilteringUseCase.emit(Async.Loading)
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
                listOf("1500 USD"),
                investments,
            )
        )
        val fakeNetWorth = InvestmentNetWorth(
            listOf("500 USD"),
        )
        val fakeCategories = listOf(
            InvestmentCategory(
                id = "1",
                name = "etfs",
                values = listOf("500 USD"),
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
            )
        )
        val fakeInvestmentsFiltering = listOf(
            FilterMenuItem(type = "stocks", label = "stocks", size = 1),
        )

        fakeObserveGroupedInvestmentsUseCase.emit(Async.Success(fakeInvestments))
        fakeObserveInvestmentNetWorthUseCase.emit(Async.Success(fakeNetWorth))
        fakeObserveInvestmentCategoriesUseCase.emit(Async.Success(fakeCategories))
        fakeObserveInvestmentAssetsUseCase.emit(Async.Success(fakeAssets))
        fakeObserveInvestmentsFilteringUseCase.emit(Async.Success(fakeInvestmentsFiltering))
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
        assertFalse(state.isLoading)
        assertFalse(state.hasError)
        job.cancel()
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveGroupedInvestmentsUseCase.emit(Async.Error)
        fakeObserveInvestmentNetWorthUseCase.emit(Async.Error)
        fakeObserveInvestmentCategoriesUseCase.emit(Async.Error)
        fakeObserveInvestmentAssetsUseCase.emit(Async.Error)
        fakeObserveInvestmentsFilteringUseCase.emit(Async.Success(listOf()))
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
                listOf("1500 USD"),
                investments,
            )
        )
        val fakeNetWorth = InvestmentNetWorth(
            values = listOf("500 USD"),
        )
        val fakeCategories = listOf(
            InvestmentCategory(
                id = "1",
                name = "etfs",
                values = listOf("500 USD"),
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
            )
        )
        fakeObserveGroupedInvestmentsUseCase.emit(Async.Success(fakeInvestments))
        fakeObserveInvestmentNetWorthUseCase.emit(Async.Success(fakeNetWorth))
        fakeObserveInvestmentCategoriesUseCase.emit(Async.Success(fakeCategories))
        fakeObserveInvestmentAssetsUseCase.emit(Async.Success(fakeAssets))
        fakeObserveInvestmentsFilteringUseCase.emit(Async.Success(listOf()))
        viewModel = createViewModel()

        viewModel.onIntent(InvestmentsIntent.SearchQueryChanged("snp"))

        advanceUntilIdle()

        assertEquals("snp", fakeObserveGroupedInvestmentsUseCase.lastQuery)
        assertEquals("snp", fakeObserveInvestmentNetWorthUseCase.lastQuery)
        assertEquals("snp", fakeObserveInvestmentCategoriesUseCase.lastQuery)
        assertEquals("snp", fakeObserveInvestmentAssetsUseCase.lastQuery)
    }

    @Test
    fun `setFiltering updates saved state handle`() = runTest {
        fakeObserveGroupedInvestmentsUseCase.emit(Async.Success(listOf()))
        fakeObserveInvestmentNetWorthUseCase.emit(Async.Success(InvestmentNetWorth(listOf())))
        fakeObserveInvestmentCategoriesUseCase.emit(Async.Success(listOf()))
        fakeObserveInvestmentAssetsUseCase.emit(Async.Success(listOf()))
        fakeObserveInvestmentsFilteringUseCase.emit(Async.Success(listOf()))
        viewModel = createViewModel()

        val expected = "etfs"

        viewModel.onIntent(InvestmentsIntent.SetFiltering(expected))
        advanceUntilIdle()

        assertEquals(
            expected,
            savedStateHandle[INVESTMENTS_FILTER_SAVED_STATE_KEY]
        )
    }

    private fun createViewModel() = InvestmentsViewModel(
        fakeObserveGroupedInvestmentsUseCase,
        fakeObserveInvestmentNetWorthUseCase,
        fakeObserveInvestmentCategoriesUseCase,
        fakeObserveInvestmentAssetsUseCase,
        fakeObserveInvestmentsFilteringUseCase,
        savedStateHandle
    )
}