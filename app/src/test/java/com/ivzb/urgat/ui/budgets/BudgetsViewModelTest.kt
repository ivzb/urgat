package com.ivzb.urgat.ui.budgets

import com.ivzb.urgat.data.finance.FakeFinanceRepository
import com.ivzb.urgat.R
import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.budget.BudgetCategory
import com.ivzb.urgat.data.budget.GroupedBudget
import com.ivzb.urgat.domain.budget.FakeObserveBudgetsUiStateUseCase
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
import java.math.BigDecimal
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BudgetsViewModelTest {

    private lateinit var fakeFinanceRepository: FakeFinanceRepository
    private lateinit var fakeObserveBudgetsUiStateUseCase: FakeObserveBudgetsUiStateUseCase

    private lateinit var viewModel: BudgetsViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        fakeFinanceRepository = FakeFinanceRepository()
        fakeObserveBudgetsUiStateUseCase = FakeObserveBudgetsUiStateUseCase()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveBudgetsUiStateUseCase.emit(Async.Loading)
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        val initial = viewModel.uiState.value
        assertTrue(initial.isLoading)
        assertFalse(initial.hasError)
        assertTrue(initial.budgets.isEmpty())
        assertTrue(initial.monthlyBudgets.isEmpty())
        assertTrue(initial.yearlyBudgets.isEmpty())
        assertNull(initial.allTimeBudget)

        job.cancel()
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        val fakeBudgets = listOf(
            Budget("1", BigDecimal("6500"), BigDecimal("6500"), BigDecimal("-1250"), "EUR", 1L, false, 1L),
        )
        val fakeMonthlyBudgets = listOf(
            GroupedBudget(
                category = GroupedBudgetCategories(
                    group = "2025/11",
                    total = 6800.toBigDecimal(),
                    items = listOf(
                        BudgetCategory("1", "1", "EUR", 6800.toBigDecimal()),
                    ),
                ),
                currency = "EUR",
                totalIncome = 6800.toBigDecimal(),
                totalExpenses = 1200.toBigDecimal(),
                totalInvestments = 1_800.toBigDecimal(),
                totalSavings = 1100.toBigDecimal(),
            ),
        )
        val fakeYearlyBudgets = listOf(
            GroupedBudget(
                category = GroupedBudgetCategories(
                    group = "2025",
                    total = 13800.toBigDecimal(),
                    items = listOf(
                        BudgetCategory("1", "1", "EUR", 6800.toBigDecimal()),
                        BudgetCategory("2", "2", "EUR", 2600.toBigDecimal()),
                        BudgetCategory("3", "3", "EUR", 1000.toBigDecimal()),
                        BudgetCategory("4", "4", "EUR", 3400.toBigDecimal()),
                    ),
                ),
                currency = "EUR",
                totalIncome = 13800.toBigDecimal(),
                totalExpenses = 2600.toBigDecimal(),
                totalInvestments = 3_800.toBigDecimal(),
                totalSavings = 1800.toBigDecimal(),
            ),
        )
        val fakeAllTimeBudgets = GroupedBudget(
            category = GroupedBudgetCategories(
                group = "",
                total = 20400.toBigDecimal(),
                items = listOf(
                    BudgetCategory("", "", "EUR", 20400.toBigDecimal()),
                ),
            ),
            currency = "EUR",
            totalIncome = 20400.toBigDecimal(),
            totalExpenses = 2600.toBigDecimal(),
            totalInvestments = 3_800.toBigDecimal(),
            totalSavings = 1500.toBigDecimal(),
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

        val fakeBudgetsUiState = BudgetsUiState(
            budgets = fakeBudgets,
            monthlyBudgets = fakeMonthlyBudgets,
            yearlyBudgets = fakeYearlyBudgets,
            allTimeBudget = fakeAllTimeBudgets,
            filteringUiInfo = BudgetsFilteringUiInfo(
                filterMenuItems = fakeBudgetsFiltering,
                sortMenuItems = fakeBudgetsSorting,
            )
        )
        fakeObserveBudgetsUiStateUseCase.emit(Async.Success(fakeBudgetsUiState))

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
        fakeObserveBudgetsUiStateUseCase.emit(Async.Error)
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
        val fakeBudgets = listOf(
            Budget("1", BigDecimal("6500"), BigDecimal("6500"), BigDecimal("-1250"), "EUR", 1L, false, 1L),
        )
        val fakeCategoriesMonthly = listOf(
            GroupedBudget(
                category = GroupedBudgetCategories(
                    group = "2025/11",
                    total = 6800.toBigDecimal(),
                    items = listOf(
                        BudgetCategory("1", "1", "EUR", 6800.toBigDecimal()),
                    ),
                ),
                currency = "EUR",
                totalIncome = 6800.toBigDecimal(),
                totalExpenses = 1200.toBigDecimal(),
                totalInvestments = 1_800.toBigDecimal(),
                totalSavings = 1100.toBigDecimal(),
            ),
        )
        val fakeCategoriesYearly = listOf(
            GroupedBudget(
                category = GroupedBudgetCategories(
                    group = "2025",
                    total = 13800.toBigDecimal(),
                    items = listOf(
                        BudgetCategory("1", "1", "EUR", 6800.toBigDecimal()),
                        BudgetCategory("2", "2", "EUR", 2600.toBigDecimal()),
                        BudgetCategory("3", "3", "EUR", 1000.toBigDecimal()),
                        BudgetCategory("4", "4", "EUR", 3400.toBigDecimal()),
                    ),
                ),
                currency = "EUR",
                totalIncome = 13800.toBigDecimal(),
                totalExpenses = 2600.toBigDecimal(),
                totalInvestments = 3_800.toBigDecimal(),
                totalSavings = 1800.toBigDecimal(),
            ),
        )
        val fakeCategoriesAllTime = GroupedBudget(
            category = GroupedBudgetCategories(
                group = "",
                total = 20400.toBigDecimal(),
                items = listOf(
                    BudgetCategory("", "", "EUR", 20400.toBigDecimal()),
                ),
            ),
            currency = "EUR",
            totalIncome = 20400.toBigDecimal(),
            totalExpenses = 2600.toBigDecimal(),
            totalInvestments = 3_800.toBigDecimal(),
            totalSavings = 1500.toBigDecimal(),
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

        val fakeBudgetsUiState = BudgetsUiState(
            budgets = fakeBudgets,
            monthlyBudgets = fakeCategoriesMonthly,
            yearlyBudgets = fakeCategoriesYearly,
            allTimeBudget = fakeCategoriesAllTime,
            filteringUiInfo = BudgetsFilteringUiInfo(
                filterMenuItems = fakeBudgetsFiltering,
                sortMenuItems = fakeBudgetsSorting,
            )
        )
        fakeObserveBudgetsUiStateUseCase.emit(Async.Success(fakeBudgetsUiState))

        viewModel = createViewModel()

        viewModel.onIntent(BudgetsIntent.SearchQueryChanged("snp"))

        advanceUntilIdle()

        assertEquals("snp", fakeObserveBudgetsUiStateUseCase.lastQuery)
    }

    @Test
    fun `setFiltering updates finance repository`() = runTest {
        viewModel = createViewModel()

        val expected = BudgetsFilterType.POSITIVE_BUDGETS

        viewModel.onIntent(BudgetsIntent.SetFiltering(expected))
        advanceUntilIdle()

        assertEquals(expected, fakeFinanceRepository.budgetsFilterTypeFlow.value)
    }

    @Test
    fun `setSorting updates finance repository`() = runTest {
        viewModel = createViewModel()

        val expected = BudgetsSortType.BY_MIN

        viewModel.onIntent(BudgetsIntent.SetSorting(expected))
        advanceUntilIdle()

        assertEquals(expected, fakeFinanceRepository.budgetsSortTypeFlow.value)
    }

    @Test
    fun `resetFilters resets search query, filter type, and sort type to defaults`() = runTest {
        viewModel = createViewModel()

        viewModel.onIntent(BudgetsIntent.SearchQueryChanged("rent"))
        viewModel.onIntent(BudgetsIntent.SetFiltering(BudgetsFilterType.POSITIVE_BUDGETS))
        viewModel.onIntent(BudgetsIntent.SetSorting(BudgetsSortType.BY_MAX))
        advanceUntilIdle()

        assertEquals("rent", fakeFinanceRepository.searchQueryFlow.value)
        assertEquals(BudgetsFilterType.POSITIVE_BUDGETS, fakeFinanceRepository.budgetsFilterTypeFlow.value)
        assertEquals(BudgetsSortType.BY_MAX, fakeFinanceRepository.budgetsSortTypeFlow.value)

        viewModel.onIntent(BudgetsIntent.ResetFilters)
        advanceUntilIdle()

        assertEquals("", fakeFinanceRepository.searchQueryFlow.value)
        assertEquals(BudgetsFilterType.ALL_BUDGETS, fakeFinanceRepository.budgetsFilterTypeFlow.value)
        assertEquals(BudgetsSortType.BY_DATE, fakeFinanceRepository.budgetsSortTypeFlow.value)
    }

    private fun createViewModel() = BudgetsViewModel(
        fakeObserveBudgetsUiStateUseCase,
        fakeFinanceRepository,
    )
}