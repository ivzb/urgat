package com.ivzb.urgat.ui.savings

import com.ivzb.urgat.data.finance.FakeFinanceRepository
import com.ivzb.urgat.R
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.saving.SavingCategory
import com.ivzb.urgat.domain.saving.FakeObserveSavingsUiStateUseCase
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
class SavingsViewModelTest {

    private lateinit var fakeFinanceRepository: FakeFinanceRepository
    private lateinit var fakeObserveSavingsUiStateUseCase: FakeObserveSavingsUiStateUseCase

    private lateinit var viewModel: SavingsViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeFinanceRepository = FakeFinanceRepository()
        fakeObserveSavingsUiStateUseCase = FakeObserveSavingsUiStateUseCase()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveSavingsUiStateUseCase.emit(Async.Loading)
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        val initial = viewModel.uiState.value
        assertTrue(initial.isLoading)
        assertFalse(initial.hasError)
        assertTrue(initial.savings.isEmpty())
        assertTrue(initial.categoriesMonthly.isEmpty())
        assertTrue(initial.categoriesYearly.isEmpty())
        assertTrue(initial.categoriesAllTime.isEmpty())

        job.cancel()
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        val savings = listOf(Saving("1", "Emergency fund", BigDecimal.TEN, "EUR", 1L, false, 1L))
        val fakeSavings = listOf(
            GroupedItems(
                group = "2025/12",
                total = "10".toBigDecimal(),
                items = savings,
            )
        )
        val fakeCategoriesMonthly = listOf(
            GroupedItems(
                group = "2025/12",
                total = "10".toBigDecimal(),
                items = listOf(
                    SavingCategory(
                        id = "1",
                        name = "food",
                        value = "10".toBigDecimal(),
                        savings = savings,
                    )
                )
            )
        )
        val fakeCategoriesYearly = listOf(
            GroupedItems(
                group = "2025",
                total = "10".toBigDecimal(),
                items = listOf(
                    SavingCategory(
                        id = "1",
                        name = "food",
                        value = "10".toBigDecimal(),
                        savings = savings,
                    )
                )
            )
        )
        val fakeCategoriesAllTime = listOf(
            SavingCategory(
                id = "1",
                name = "food",
                value = "10".toBigDecimal(),
                savings = savings,
            )
        )
        val fakeSavingsSorting = listOf(
            SortMenuItem(type = SavingsSortType.BY_DATE, labelResId = R.string.sort_by_date),
            SortMenuItem(type = SavingsSortType.BY_MIN, labelResId = R.string.sort_by_min),
            SortMenuItem(type = SavingsSortType.BY_MAX, labelResId = R.string.sort_by_max),
        )

        val fakeSavingsUiState = SavingsUiState(
            savings = fakeSavings,
            categoriesMonthly = fakeCategoriesMonthly,
            categoriesYearly = fakeCategoriesYearly,
            categoriesAllTime = fakeCategoriesAllTime,
            filteringUiInfo = SavingsFilteringUiInfo(
                sortMenuItems = fakeSavingsSorting,
            ),
        )
        fakeObserveSavingsUiStateUseCase.emit(Async.Success(fakeSavingsUiState))

        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(fakeSavings, state.savings)
        assertEquals(fakeCategoriesMonthly, state.categoriesMonthly)
        assertEquals(fakeCategoriesYearly, state.categoriesYearly)
        assertEquals(fakeCategoriesAllTime, state.categoriesAllTime)
        assertEquals(fakeSavingsSorting, state.filteringUiInfo.sortMenuItems)
        assertFalse(state.isLoading)
        assertFalse(state.hasError)

        job.cancel()
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveSavingsUiStateUseCase.emit(Async.Error)
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
        val savings = listOf(Saving("1", "Emergency fund", BigDecimal.TEN, "EUR", 1L, false, 1L))
        val fakeSavings = listOf(GroupedItems(
            group = "2025/12",
            total = "10".toBigDecimal(),
            items = savings,
        ))
        val fakeCategoriesMonthly = listOf(
            GroupedItems(
                group = "2025/12",
                total = "10".toBigDecimal(),
                items = listOf(
                    SavingCategory(
                        id = "1",
                        name = "food",
                        value = "10".toBigDecimal(),
                        savings = savings,
                    )
                )
            )
        )
        val fakeCategoriesYearly = listOf(
            GroupedItems(
                group = "2025",
                total = "10".toBigDecimal(),
                items = listOf(
                    SavingCategory(
                        id = "1",
                        name = "food",
                        value = "10".toBigDecimal(),
                        savings = savings,
                    )
                )
            )
        )
        val fakeCategoriesAllTime = listOf(
            SavingCategory(
                id = "1",
                name = "food",
                value = "10".toBigDecimal(),
                savings = savings,
            )
        )
        val fakeSavingsSorting = listOf(
            SortMenuItem(type = SavingsSortType.BY_DATE, labelResId = R.string.sort_by_date),
            SortMenuItem(type = SavingsSortType.BY_MIN, labelResId = R.string.sort_by_min),
            SortMenuItem(type = SavingsSortType.BY_MAX, labelResId = R.string.sort_by_max),
        )

        val fakeSavingsUiState = SavingsUiState(
            savings = fakeSavings,
            categoriesMonthly = fakeCategoriesMonthly,
            categoriesYearly = fakeCategoriesYearly,
            categoriesAllTime = fakeCategoriesAllTime,
            filteringUiInfo = SavingsFilteringUiInfo(
                sortMenuItems = fakeSavingsSorting,
            ),
        )
        fakeObserveSavingsUiStateUseCase.emit(Async.Success(fakeSavingsUiState))

        viewModel = createViewModel()

        viewModel.onIntent(SavingsIntent.SearchQueryChanged("coffee"))

        advanceUntilIdle()

        assertEquals("coffee", fakeObserveSavingsUiStateUseCase.lastQuery)
    }

    @Test
    fun `setFiltering updates finance repository`() = runTest {
        viewModel = createViewModel()

        val expected = SavingsSortType.BY_MIN

        viewModel.onIntent(SavingsIntent.SetSorting(expected))
        advanceUntilIdle()
    }

    @Test
    fun `resetFilters resets search query and sort type to defaults`() = runTest {
        viewModel = createViewModel()

        viewModel.onIntent(SavingsIntent.SearchQueryChanged("emergency fund"))
        viewModel.onIntent(SavingsIntent.SetSorting(SavingsSortType.BY_MAX))
        advanceUntilIdle()

        assertEquals("emergency fund", fakeFinanceRepository.searchQueryFlow.value)
        assertEquals(SavingsSortType.BY_MAX, fakeFinanceRepository.savingsSortTypeFlow.value)

        viewModel.onIntent(SavingsIntent.ResetFilters)
        advanceUntilIdle()

        assertEquals("", fakeFinanceRepository.searchQueryFlow.value)
        assertEquals(SavingsSortType.BY_DATE, fakeFinanceRepository.savingsSortTypeFlow.value)
    }

    private fun createViewModel() = SavingsViewModel(
        fakeObserveSavingsUiStateUseCase,
        fakeFinanceRepository,
    )
}