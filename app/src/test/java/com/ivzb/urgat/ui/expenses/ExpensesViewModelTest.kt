package com.ivzb.urgat.ui.expenses

import androidx.lifecycle.SavedStateHandle
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import com.ivzb.urgat.R
import com.ivzb.urgat.data.expense.ExpenseCategory
import com.ivzb.urgat.domain.expense.FakeObserveExpensesFilteringUseCase
import com.ivzb.urgat.domain.expense.FakeObserveGroupedExpenseCategoriesAllTimeUseCase
import com.ivzb.urgat.domain.expense.FakeObserveGroupedExpenseCategoriesMonthlyUseCase
import com.ivzb.urgat.domain.expense.FakeObserveGroupedExpenseCategoriesYearlyUseCase
import com.ivzb.urgat.domain.expense.FakeObserveGroupedExpensesUseCase
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.ui.GroupedItems
import kotlinx.coroutines.flow.collect
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class ExpensesViewModelTest {

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var fakeObserveExpensesUseCase: FakeObserveGroupedExpensesUseCase
    private lateinit var fakeObserveGroupedExpenseCategoriesMonthlyUseCase: FakeObserveGroupedExpenseCategoriesMonthlyUseCase
    private lateinit var fakeObserveGroupedExpenseCategoriesYearlyUseCase: FakeObserveGroupedExpenseCategoriesYearlyUseCase
    private lateinit var fakeObserveGroupedExpenseCategoriesAllTimeUseCase: FakeObserveGroupedExpenseCategoriesAllTimeUseCase
    private lateinit var fakeObserveExpensesFilteringUseCase: FakeObserveExpensesFilteringUseCase

    private lateinit var viewModel: ExpensesViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle(mapOf(EXPENSES_FILTER_SAVED_STATE_KEY to null))
        fakeObserveExpensesUseCase = FakeObserveGroupedExpensesUseCase()
        fakeObserveGroupedExpenseCategoriesMonthlyUseCase = FakeObserveGroupedExpenseCategoriesMonthlyUseCase()
        fakeObserveGroupedExpenseCategoriesYearlyUseCase = FakeObserveGroupedExpenseCategoriesYearlyUseCase()
        fakeObserveGroupedExpenseCategoriesAllTimeUseCase = FakeObserveGroupedExpenseCategoriesAllTimeUseCase()
        fakeObserveExpensesFilteringUseCase = FakeObserveExpensesFilteringUseCase()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveExpensesUseCase.emit(Async.Loading)
        fakeObserveGroupedExpenseCategoriesMonthlyUseCase.emit(Async.Loading)
        fakeObserveGroupedExpenseCategoriesYearlyUseCase.emit(Async.Loading)
        fakeObserveGroupedExpenseCategoriesAllTimeUseCase.emit(Async.Loading)
        fakeObserveExpensesFilteringUseCase.emit(Async.Loading)
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        val initial = viewModel.uiState.value
        assertTrue(initial.isLoading)
        assertFalse(initial.hasError)
        assertTrue(initial.expenses.isEmpty())
        assertTrue(initial.categoriesMonthly.isEmpty())
        assertTrue(initial.categoriesYearly.isEmpty())
        assertTrue(initial.categoriesAllTime.isEmpty())

        job.cancel()
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        val expenses = listOf(Expense("1", "Lunch", BigDecimal.TEN, "food", 1L, false, false, 1L))
        val fakeExpenses = listOf(
            GroupedItems(
                group = "2025/12",
                total = "10".toBigDecimal(),
                items = expenses,
            )
        )
        val fakeCategoriesMonthly = listOf(
            GroupedItems(
                group = "2025/12",
                total = "10".toBigDecimal(),
                items = listOf(
                    ExpenseCategory(
                        id = "1",
                        name = "food",
                        value = "10".toBigDecimal(),
                        expenses = expenses,
                    )
                )
            )
        )
        val fakeCategoriesYearly = listOf(
            GroupedItems(
                group = "2025",
                total = "10".toBigDecimal(),
                items = listOf(
                    ExpenseCategory(
                        id = "1",
                        name = "food",
                        value = "10".toBigDecimal(),
                        expenses = expenses,
                    )
                )
            )
        )
        val fakeCategoriesAllTime = listOf(
            ExpenseCategory(
                id = "1",
                name = "food",
                value = "10".toBigDecimal(),
                expenses = expenses,
            )
        )
        val fakeExpensesFiltering = listOf(
            FilterMenuItem(type = ExpensesFilterType.ALL_EXPENSES, labelResId = R.string.filter_all, size = 1),
            FilterMenuItem(type = ExpensesFilterType.DEBIT_EXPENSES, labelResId = R.string.filter_debit, size = 1),
            FilterMenuItem(type = ExpensesFilterType.COST_EXPENSES, labelResId = R.string.filter_cost, size = 0),
        )

        fakeObserveExpensesUseCase.emit(Async.Success(fakeExpenses))
        fakeObserveGroupedExpenseCategoriesMonthlyUseCase.emit(Async.Success(fakeCategoriesMonthly))
        fakeObserveGroupedExpenseCategoriesYearlyUseCase.emit(Async.Success(fakeCategoriesYearly))
        fakeObserveGroupedExpenseCategoriesAllTimeUseCase.emit(Async.Success(fakeCategoriesAllTime))
        fakeObserveExpensesFilteringUseCase.emit(Async.Success(fakeExpensesFiltering))
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(fakeExpenses, state.expenses)
        assertEquals(fakeCategoriesMonthly, state.categoriesMonthly)
        assertEquals(fakeCategoriesYearly, state.categoriesYearly)
        assertEquals(fakeCategoriesAllTime, state.categoriesAllTime)
        assertEquals(fakeExpensesFiltering, state.filteringUiInfo.filterMenuItems)
        assertFalse(state.isLoading)
        assertFalse(state.hasError)

        job.cancel()
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveExpensesUseCase.emit(Async.Error)
        fakeObserveGroupedExpenseCategoriesMonthlyUseCase.emit(Async.Error)
        fakeObserveGroupedExpenseCategoriesYearlyUseCase.emit(Async.Error)
        fakeObserveGroupedExpenseCategoriesAllTimeUseCase.emit(Async.Error)
        fakeObserveExpensesFilteringUseCase.emit(Async.Error)
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
        val expenses = listOf(Expense("1", "Lunch", BigDecimal.TEN, "food", 1L, false, false, 1L))
        val fakeExpenses = listOf(GroupedItems(
            group = "2025/12",
            total = "10".toBigDecimal(),
            items = expenses,
        ))
        val fakeCategoriesMonthly = listOf(
            GroupedItems(
                group = "2025/12",
                total = "10".toBigDecimal(),
                items = listOf(
                    ExpenseCategory(
                        id = "1",
                        name = "food",
                        value = "10".toBigDecimal(),
                        expenses = expenses,
                    )
                )
            )
        )
        val fakeCategoriesYearly = listOf(
            GroupedItems(
                group = "2025",
                total = "10".toBigDecimal(),
                items = listOf(
                    ExpenseCategory(
                        id = "1",
                        name = "food",
                        value = "10".toBigDecimal(),
                        expenses = expenses,
                    )
                )
            )
        )
        val fakeCategoriesAllTime = listOf(
            ExpenseCategory(
                id = "1",
                name = "food",
                value = "10".toBigDecimal(),
                expenses = expenses,
            )
        )

        fakeObserveExpensesUseCase.emit(Async.Success(fakeExpenses))
        fakeObserveGroupedExpenseCategoriesMonthlyUseCase.emit(Async.Success(fakeCategoriesMonthly))
        fakeObserveGroupedExpenseCategoriesYearlyUseCase.emit(Async.Success(fakeCategoriesYearly))
        fakeObserveGroupedExpenseCategoriesAllTimeUseCase.emit(Async.Success(fakeCategoriesAllTime))
        fakeObserveExpensesFilteringUseCase.emit(Async.Success(listOf()))
        viewModel = createViewModel()

        viewModel.onIntent(ExpensesIntent.SearchQueryChanged("coffee"))

        advanceUntilIdle()

        assertEquals("coffee", fakeObserveExpensesUseCase.lastQuery)
    }

    @Test
    fun `setFiltering updates saved state handle`() = runTest {
        fakeObserveExpensesUseCase.emit(Async.Success(emptyList()))
        fakeObserveGroupedExpenseCategoriesMonthlyUseCase.emit(Async.Success(emptyList()))
        fakeObserveGroupedExpenseCategoriesYearlyUseCase.emit(Async.Success(emptyList()))
        fakeObserveGroupedExpenseCategoriesAllTimeUseCase.emit(Async.Success(emptyList()))
        fakeObserveExpensesFilteringUseCase.emit(Async.Success(listOf()))
        viewModel = createViewModel()

        val expected = ExpensesFilterType.DEBIT_EXPENSES

        viewModel.onIntent(ExpensesIntent.SetFiltering(expected))
        advanceUntilIdle()

        assertEquals(expected, savedStateHandle[EXPENSES_FILTER_SAVED_STATE_KEY])
    }

    private fun createViewModel() = ExpensesViewModel(
        fakeObserveExpensesUseCase,
        fakeObserveGroupedExpenseCategoriesMonthlyUseCase,
        fakeObserveGroupedExpenseCategoriesYearlyUseCase,
        fakeObserveGroupedExpenseCategoriesAllTimeUseCase,
        fakeObserveExpensesFilteringUseCase,
        savedStateHandle
    )
}