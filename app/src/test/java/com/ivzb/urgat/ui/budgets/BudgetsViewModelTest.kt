package com.ivzb.urgat.ui.budgets

import androidx.lifecycle.SavedStateHandle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.budget.BudgetCategory
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.domain.budget.FakeObserveBudgetsFilteringUseCase
import com.ivzb.urgat.domain.budget.FakeObserveBudgetsSortingUseCase
import com.ivzb.urgat.domain.budget.FakeObserveBudgetsUseCase
import com.ivzb.urgat.domain.budget.FakeObserveGroupedBudgetCategoriesMonthlyUseCase
import com.ivzb.urgat.domain.budget.GroupedBudgetCategories
import com.ivzb.urgat.domain.expense.FakeObserveGroupedExpensesMonthlyUseCase
import com.ivzb.urgat.domain.investment.FakeObserveGroupedInvestmentsMonthlyUseCase
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
import java.math.BigDecimal
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BudgetsViewModelTest {

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var fakeObserveBudgetsUseCase: FakeObserveBudgetsUseCase
    private lateinit var fakeObserveGroupedBudgetCategoriesMonthlyUseCase: FakeObserveGroupedBudgetCategoriesMonthlyUseCase
    private lateinit var fakeObserveGroupedExpensesUseCase: FakeObserveGroupedExpensesMonthlyUseCase
    private lateinit var fakeObserveGroupedInvestmentsUseCase: FakeObserveGroupedInvestmentsMonthlyUseCase
    private lateinit var fakeObserveBudgetsFilteringUseCase: FakeObserveBudgetsFilteringUseCase
    private lateinit var fakeObserveBudgetsSortingUseCase: FakeObserveBudgetsSortingUseCase

    private lateinit var viewModel: BudgetsViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        savedStateHandle = SavedStateHandle(mapOf(BUDGETS_FILTER_SAVED_STATE_KEY to null))
        fakeObserveBudgetsUseCase = FakeObserveBudgetsUseCase()
        fakeObserveGroupedBudgetCategoriesMonthlyUseCase = FakeObserveGroupedBudgetCategoriesMonthlyUseCase()
        fakeObserveGroupedExpensesUseCase = FakeObserveGroupedExpensesMonthlyUseCase()
        fakeObserveGroupedInvestmentsUseCase = FakeObserveGroupedInvestmentsMonthlyUseCase()
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
        fakeObserveGroupedBudgetCategoriesMonthlyUseCase.emit(Async.Loading)
        fakeObserveGroupedExpensesUseCase.emit(Async.Loading)
        fakeObserveGroupedInvestmentsUseCase.emit(Async.Loading)
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
        val budgets = listOf(Budget("1", 6500.toBigDecimal(), 8800.toBigDecimal(), 7200.toBigDecimal(), 1L, false, 1L))
        val fakeBudgets = budgets
        val fakeCategoriesMonthly = listOf(
            GroupedItems(
                group = "2025/12",
                total = 6500.toBigDecimal(),
                items = listOf(
                    BudgetCategory(
                        id = "1",
                        name = "1",
                        value = 6500.toBigDecimal()
                    )
                )
            )
        )

        val investments = listOf(
            Investment(
                "1",
                "SNP 500",
                500.toBigDecimal(),
                30.toBigDecimal(),
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

        val expenses = listOf(Expense("1", "Lunch", BigDecimal.TEN, "food", 1L, false, false, 1L))
        val fakeExpenses = listOf(
            GroupedItems(
                group = "2025/12",
                total = "10".toBigDecimal(),
                items = expenses,
            )
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
        fakeObserveGroupedBudgetCategoriesMonthlyUseCase.emit(Async.Success(fakeCategoriesMonthly))
        fakeObserveGroupedExpensesUseCase.emit(Async.Success(fakeExpenses))
        fakeObserveGroupedInvestmentsUseCase.emit(Async.Success(fakeInvestments))
        fakeObserveBudgetsFilteringUseCase.emit(Async.Success(fakeBudgetsFiltering))
        fakeObserveBudgetsSortingUseCase.emit(Async.Success(fakeBudgetsSorting))
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        val expectedMonthlyBudgets = listOf(
            MonthlyBudgetUiState(
                category = GroupedBudgetCategories(
                    group = "2025/12",
                    total = 6500.toBigDecimal(),
                    items = listOf(
                        BudgetCategory(id="1", name="1", value=6500.toBigDecimal())
                    ),
                ),
                totalIncome = 6500.toBigDecimal(),
                totalExpenses = 10.toBigDecimal(),
                totalInvestments = 15000.toBigDecimal(),
                totalInvestmentsFormatted = "1500 USD",
            ),
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(fakeBudgets, state.budgets)
        assertEquals(expectedMonthlyBudgets, state.monthlyBudgets)
        assertEquals(fakeBudgetsFiltering, state.filteringUiInfo.filterMenuItems)
        assertEquals(fakeBudgetsSorting, state.filteringUiInfo.sortMenuItems)
        assertFalse(state.isLoading)
        assertFalse(state.hasError)
        job.cancel()
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveBudgetsUseCase.emit(Async.Error)
        fakeObserveGroupedBudgetCategoriesMonthlyUseCase.emit(Async.Success(listOf()))
        fakeObserveGroupedExpensesUseCase.emit(Async.Success(listOf()))
        fakeObserveGroupedInvestmentsUseCase.emit(Async.Success(listOf()))
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
        fakeObserveGroupedBudgetCategoriesMonthlyUseCase.emit(Async.Success(listOf()))
        fakeObserveGroupedExpensesUseCase.emit(Async.Success(listOf()))
        fakeObserveGroupedInvestmentsUseCase.emit(Async.Success(listOf()))
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
        fakeObserveGroupedBudgetCategoriesMonthlyUseCase.emit(Async.Success(listOf()))
        fakeObserveGroupedExpensesUseCase.emit(Async.Success(listOf()))
        fakeObserveGroupedInvestmentsUseCase.emit(Async.Success(listOf()))
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

    private fun createViewModel() = BudgetsViewModel(
        fakeObserveBudgetsUseCase,
        fakeObserveGroupedBudgetCategoriesMonthlyUseCase,
        fakeObserveGroupedExpensesUseCase,
        fakeObserveGroupedInvestmentsUseCase,
        fakeObserveBudgetsFilteringUseCase,
        fakeObserveBudgetsSortingUseCase,
        savedStateHandle
    )
}