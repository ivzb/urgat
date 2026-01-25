package com.ivzb.urgat.ui.budgets

import androidx.lifecycle.SavedStateHandle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.budget.BudgetCategory
import com.ivzb.urgat.data.budget.GroupedBudget
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.domain.budget.FakeObserveBudgetsFilteringUseCase
import com.ivzb.urgat.domain.budget.FakeObserveBudgetsSortingUseCase
import com.ivzb.urgat.domain.budget.FakeObserveBudgetsUseCase
import com.ivzb.urgat.domain.budget.FakeObserveGroupedBudgetsAllTimeUseCase
import com.ivzb.urgat.domain.budget.FakeObserveGroupedBudgetsMonthlyUseCase
import com.ivzb.urgat.domain.budget.FakeObserveGroupedBudgetsYearlyUseCase
import com.ivzb.urgat.domain.budget.GroupedBudgetCategories
import com.ivzb.urgat.ui.components.FilterMenuItem
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BudgetsViewModelTest {

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var fakeObserveBudgetsUseCase: FakeObserveBudgetsUseCase
    private lateinit var fakeObserveGroupedBudgetsMonthlyUseCase: FakeObserveGroupedBudgetsMonthlyUseCase
    private lateinit var fakeObserveGroupedBudgetsYearlyUseCase: FakeObserveGroupedBudgetsYearlyUseCase
    private lateinit var fakeObserveGroupedBudgetsAllTimeUseCase: FakeObserveGroupedBudgetsAllTimeUseCase
    private lateinit var fakeObserveBudgetsFilteringUseCase: FakeObserveBudgetsFilteringUseCase
    private lateinit var fakeObserveBudgetsSortingUseCase: FakeObserveBudgetsSortingUseCase

    private lateinit var viewModel: BudgetsViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        savedStateHandle = SavedStateHandle(mapOf(BUDGETS_FILTER_SAVED_STATE_KEY to null))
        fakeObserveBudgetsUseCase = FakeObserveBudgetsUseCase()
        fakeObserveGroupedBudgetsMonthlyUseCase = FakeObserveGroupedBudgetsMonthlyUseCase()
        fakeObserveGroupedBudgetsYearlyUseCase = FakeObserveGroupedBudgetsYearlyUseCase()
        fakeObserveGroupedBudgetsAllTimeUseCase = FakeObserveGroupedBudgetsAllTimeUseCase()
        fakeObserveBudgetsFilteringUseCase = FakeObserveBudgetsFilteringUseCase()
        fakeObserveBudgetsSortingUseCase = FakeObserveBudgetsSortingUseCase()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveBudgetsUseCase.emit(Async.Loading)
        fakeObserveGroupedBudgetsMonthlyUseCase.emit(Async.Loading)
        fakeObserveGroupedBudgetsYearlyUseCase.emit(Async.Loading)
        fakeObserveGroupedBudgetsAllTimeUseCase.emit(Async.Loading)
        fakeObserveBudgetsFilteringUseCase.emit(Async.Loading)
        fakeObserveBudgetsSortingUseCase.emit(Async.Loading)
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        val initial = viewModel.uiState.value
        assertTrue(initial.isLoading)
        assertFalse(initial.hasError)
        assertTrue(initial.budgets.isEmpty())

        job.cancel()
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        val budgets = listOf(
            Budget("1", 6500.toBigDecimal(), 8800.toBigDecimal(), 7200.toBigDecimal(), 1L, false, 1L),
            Budget("2", 5500.toBigDecimal(), 7800.toBigDecimal(), 6200.toBigDecimal(), 1L, false, 1L),
        )
        val fakeBudgets = budgets
        val fakeMonthlyBudgets = listOf(
            GroupedBudget(
                category = GroupedBudgetCategories(
                    group = "2025/12",
                    total = 6_500.toBigDecimal(),
                    items = listOf(
                        BudgetCategory(id = "1", name = "1", value = 6_500.toBigDecimal())
                    ),
                ),
                totalIncome = 6_500.toBigDecimal(),
                totalExpenses = 2_000.toBigDecimal(),
                totalInvestments = 3_000.toBigDecimal(),
                totalInvestmentsFormatted = "3000 USD",
                totalSavings = 500.toBigDecimal(),
            ),
            GroupedBudget(
                category = GroupedBudgetCategories(
                    group = "2025/11",
                    total = 5_500.toBigDecimal(),
                    items = listOf(
                        BudgetCategory(id = "1", name = "1", value = 5_500.toBigDecimal())
                    ),
                ),
                totalIncome = 5_500.toBigDecimal(),
                totalExpenses = 1_800.toBigDecimal(),
                totalInvestments = 2_600.toBigDecimal(),
                totalInvestmentsFormatted = "2600 USD",
                totalSavings = 500.toBigDecimal(),
            ),
        )
        val fakeYearlyBudgets = listOf(
            GroupedBudget(
                category = GroupedBudgetCategories(
                    group = "2025",
                    total = 12_000.toBigDecimal(),
                    items = listOf(
                        BudgetCategory(id = "1", name = "1", value = 12_000.toBigDecimal())
                    ),
                ),
                totalIncome = 12_000.toBigDecimal(),
                totalExpenses = 3_800.toBigDecimal(),
                totalInvestments = 5_600.toBigDecimal(),
                totalInvestmentsFormatted = "5600 USD",
                totalSavings = 500.toBigDecimal(),
            ),
        )
        val fakeAllTimeBudgets = GroupedBudget(
            category = GroupedBudgetCategories(
                group = "All time",
                total = 24_000.toBigDecimal(),
                items = listOf(
                    BudgetCategory(id = "1", name = "1", value = 24_000.toBigDecimal())
                ),
            ),
            totalIncome = 24_000.toBigDecimal(),
            totalExpenses = 7_600.toBigDecimal(),
            totalInvestments = 11_200.toBigDecimal(),
            totalInvestmentsFormatted = "11200 USD",
            totalSavings = 500.toBigDecimal(),
        )
        val fakeBudgetsFiltering = listOf(
            FilterMenuItem(type = BudgetsFilterType.ALL_BUDGETS, labelResId = R.string.filter_all, size = 1),
            FilterMenuItem(type = BudgetsFilterType.POSITIVE_BUDGETS, labelResId = R.string.filter_positive, size = 1),
            FilterMenuItem(type = BudgetsFilterType.NEGATIVE_BUDGETS, labelResId = R.string.filter_negative, size = 0),
        )
        val fakeBudgetsSorting = listOf(
            SortMenuItem(type = BudgetsSortType.BY_DATE, labelResId = R.string.sort_by_date),
            SortMenuItem(type = BudgetsSortType.BY_MIN, labelResId = R.string.sort_by_min),
            SortMenuItem(type = BudgetsSortType.BY_MAX, labelResId = R.string.sort_by_max),
        )

        fakeObserveBudgetsUseCase.emit(Async.Success(fakeBudgets))
        fakeObserveGroupedBudgetsMonthlyUseCase.emit(Async.Success(fakeMonthlyBudgets))
        fakeObserveGroupedBudgetsYearlyUseCase.emit(Async.Success(fakeYearlyBudgets))
        fakeObserveGroupedBudgetsAllTimeUseCase.emit(Async.Success(fakeAllTimeBudgets))
        fakeObserveBudgetsFilteringUseCase.emit(Async.Success(fakeBudgetsFiltering))
        fakeObserveBudgetsSortingUseCase.emit(Async.Success(fakeBudgetsSorting))
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(fakeBudgets, state.budgets)
        assertEquals(fakeMonthlyBudgets, state.monthlyBudgets)
        assertEquals(fakeYearlyBudgets, state.yearlyBudgets)
        assertEquals(fakeAllTimeBudgets, state.allTimeBudget)
        assertEquals(fakeBudgetsFiltering, state.filteringUiInfo.filterMenuItems)
        assertEquals(fakeBudgetsSorting, state.filteringUiInfo.sortMenuItems)
        assertFalse(state.isLoading)
        assertFalse(state.hasError)
        job.cancel()
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveBudgetsUseCase.emit(Async.Error)
        fakeObserveGroupedBudgetsMonthlyUseCase.emit(Async.Success(listOf()))
        fakeObserveGroupedBudgetsYearlyUseCase.emit(Async.Success(listOf()))
        fakeObserveGroupedBudgetsAllTimeUseCase.emit(Async.Success(GroupedBudget(
            category = GroupedBudgetCategories(
                group = "All time",
                total = 24_000.toBigDecimal(),
                items = listOf(
                    BudgetCategory(id = "1", name = "1", value = 24_000.toBigDecimal())
                ),
            ),
            totalIncome = 24_000.toBigDecimal(),
            totalExpenses = 7_600.toBigDecimal(),
            totalInvestments = 11_200.toBigDecimal(),
            totalInvestmentsFormatted = "11200 USD",
            totalSavings = 500.toBigDecimal(),
        )))
        fakeObserveBudgetsFilteringUseCase.emit(Async.Success(listOf()))
        fakeObserveBudgetsSortingUseCase.emit(Async.Success(listOf()))
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
        val fakeBudgets = SampleData.SAMPLE_BUDGETS

        fakeObserveBudgetsUseCase.emit(Async.Success(fakeBudgets))
        fakeObserveGroupedBudgetsMonthlyUseCase.emit(Async.Success(listOf()))
        fakeObserveGroupedBudgetsYearlyUseCase.emit(Async.Success(listOf()))
        fakeObserveGroupedBudgetsAllTimeUseCase.emit(Async.Success(GroupedBudget(
            category = GroupedBudgetCategories(
                group = "All time",
                total = 24_000.toBigDecimal(),
                items = listOf(
                    BudgetCategory(id = "1", name = "1", value = 24_000.toBigDecimal())
                ),
            ),
            totalIncome = 24_000.toBigDecimal(),
            totalExpenses = 7_600.toBigDecimal(),
            totalInvestments = 11_200.toBigDecimal(),
            totalInvestmentsFormatted = "11200 USD",
            totalSavings = 500.toBigDecimal(),
        )))
        fakeObserveBudgetsFilteringUseCase.emit(Async.Success(listOf()))
        fakeObserveBudgetsSortingUseCase.emit(Async.Success(listOf()))
        viewModel = createViewModel()

        viewModel.onIntent(BudgetsIntent.SearchQueryChanged("snp"))

        advanceUntilIdle()

        assertEquals("snp", fakeObserveBudgetsUseCase.lastQuery)
    }

    @Test
    fun `setFiltering updates saved state handle`() = runTest {
        fakeObserveBudgetsUseCase.emit(Async.Success(listOf()))
        fakeObserveGroupedBudgetsMonthlyUseCase.emit(Async.Success(listOf()))
        fakeObserveGroupedBudgetsYearlyUseCase.emit(Async.Success(listOf()))
        fakeObserveGroupedBudgetsAllTimeUseCase.emit(Async.Success(GroupedBudget(
            category = GroupedBudgetCategories(
                group = "All time",
                total = 24_000.toBigDecimal(),
                items = listOf(
                    BudgetCategory(id = "1", name = "1", value = 24_000.toBigDecimal())
                ),
            ),
            totalIncome = 24_000.toBigDecimal(),
            totalExpenses = 7_600.toBigDecimal(),
            totalInvestments = 11_200.toBigDecimal(),
            totalInvestmentsFormatted = "11200 USD",
            totalSavings = 500.toBigDecimal(),
        )))
        fakeObserveBudgetsFilteringUseCase.emit(Async.Success(listOf()))
        fakeObserveBudgetsSortingUseCase.emit(Async.Success(listOf()))
        viewModel = createViewModel()

        val expected = BudgetsFilterType.POSITIVE_BUDGETS

        viewModel.onIntent(BudgetsIntent.SetFiltering(expected))
        advanceUntilIdle()

        assertEquals(
            expected,
            savedStateHandle[BUDGETS_FILTER_SAVED_STATE_KEY]
        )
    }

    @Test
    fun `setSorting updates saved state handle`() = runTest {
        fakeObserveBudgetsUseCase.emit(Async.Success(listOf()))
        fakeObserveGroupedBudgetsMonthlyUseCase.emit(Async.Success(listOf()))
        fakeObserveGroupedBudgetsYearlyUseCase.emit(Async.Success(listOf()))
        fakeObserveGroupedBudgetsAllTimeUseCase.emit(Async.Success(GroupedBudget(
            category = GroupedBudgetCategories(
                group = "All time",
                total = 24_000.toBigDecimal(),
                items = listOf(
                    BudgetCategory(id = "1", name = "1", value = 24_000.toBigDecimal())
                ),
            ),
            totalIncome = 24_000.toBigDecimal(),
            totalExpenses = 7_600.toBigDecimal(),
            totalInvestments = 11_200.toBigDecimal(),
            totalInvestmentsFormatted = "11200 USD",
            totalSavings = 500.toBigDecimal(),
        )))
        fakeObserveBudgetsFilteringUseCase.emit(Async.Success(listOf()))
        fakeObserveBudgetsSortingUseCase.emit(Async.Success(listOf()))
        viewModel = createViewModel()

        val expected = BudgetsSortType.BY_MIN

        viewModel.onIntent(BudgetsIntent.SetSorting(expected))
        advanceUntilIdle()

        assertEquals(
            expected,
            savedStateHandle[BUDGETS_SORT_SAVED_STATE_KEY]
        )
    }

    private fun createViewModel() = BudgetsViewModel(
        fakeObserveBudgetsUseCase,
        fakeObserveGroupedBudgetsMonthlyUseCase,
        fakeObserveGroupedBudgetsYearlyUseCase,
        fakeObserveGroupedBudgetsAllTimeUseCase,
        fakeObserveBudgetsFilteringUseCase,
        fakeObserveBudgetsSortingUseCase,
        savedStateHandle
    )
}