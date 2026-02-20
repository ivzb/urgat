package com.ivzb.urgat.ui.expenses

import androidx.lifecycle.SavedStateHandle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpenseCategory
import com.ivzb.urgat.domain.expense.FakeObserveExpensesUiStateUseCase
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
class ExpensesViewModelTest {

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var fakeObserveExpensesUiStateUseCase: FakeObserveExpensesUiStateUseCase

    private lateinit var viewModel: ExpensesViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle(mapOf())
        fakeObserveExpensesUiStateUseCase = FakeObserveExpensesUiStateUseCase()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveExpensesUiStateUseCase.emit(Async.Loading)
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
            FilterMenuItem(type = ExpensesFilterType.EXCLUDED_FROM_ANALYTICS, labelResId = R.string.filter_excluded_from_analytics, size = 0),
        )
        val fakeExpensesSorting = listOf(
            SortMenuItem(type = ExpensesSortType.BY_DATE, labelResId = R.string.sort_by_date),
            SortMenuItem(type = ExpensesSortType.BY_MIN, labelResId = R.string.sort_by_min),
            SortMenuItem(type = ExpensesSortType.BY_MAX, labelResId = R.string.sort_by_max),
        )

        val fakeExpensesUiState = ExpensesUiState(
            expenses = fakeExpenses,
            categoriesMonthly = fakeCategoriesMonthly,
            categoriesYearly = fakeCategoriesYearly,
            categoriesAllTime = fakeCategoriesAllTime,
            filteringUiInfo = ExpensesFilteringUiInfo(
                filterMenuItems = fakeExpensesFiltering,
                sortMenuItems = fakeExpensesSorting,
            )
        )
        fakeObserveExpensesUiStateUseCase.emit(Async.Success(fakeExpensesUiState))

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
        assertEquals(fakeExpensesSorting, state.filteringUiInfo.sortMenuItems)
        assertFalse(state.isLoading)
        assertFalse(state.hasError)

        job.cancel()
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveExpensesUiStateUseCase.emit(Async.Error)
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
        val fakeExpensesFiltering = listOf(
            FilterMenuItem(type = ExpensesFilterType.ALL_EXPENSES, labelResId = R.string.filter_all, size = 1),
            FilterMenuItem(type = ExpensesFilterType.DEBIT_EXPENSES, labelResId = R.string.filter_debit, size = 1),
            FilterMenuItem(type = ExpensesFilterType.COST_EXPENSES, labelResId = R.string.filter_cost, size = 0),
            FilterMenuItem(type = ExpensesFilterType.EXCLUDED_FROM_ANALYTICS, labelResId = R.string.filter_excluded_from_analytics, size = 0),
        )
        val fakeExpensesSorting = listOf(
            SortMenuItem(type = ExpensesSortType.BY_DATE, labelResId = R.string.sort_by_date),
            SortMenuItem(type = ExpensesSortType.BY_MIN, labelResId = R.string.sort_by_min),
            SortMenuItem(type = ExpensesSortType.BY_MAX, labelResId = R.string.sort_by_max),
        )

        val fakeExpensesUiState = ExpensesUiState(
            expenses = fakeExpenses,
            categoriesMonthly = fakeCategoriesMonthly,
            categoriesYearly = fakeCategoriesYearly,
            categoriesAllTime = fakeCategoriesAllTime,
            filteringUiInfo = ExpensesFilteringUiInfo(
                filterMenuItems = fakeExpensesFiltering,
                sortMenuItems = fakeExpensesSorting,
            )
        )
        fakeObserveExpensesUiStateUseCase.emit(Async.Success(fakeExpensesUiState))

        viewModel = createViewModel()

        viewModel.onIntent(ExpensesIntent.SearchQueryChanged("coffee"))

        advanceUntilIdle()

        assertEquals("coffee", fakeObserveExpensesUiStateUseCase.lastQuery)
    }

    @Test
    fun `setFiltering updates saved state handle`() = runTest {
        viewModel = createViewModel()

        val expected = ExpensesFilterType.DEBIT_EXPENSES

        viewModel.onIntent(ExpensesIntent.SetFiltering(expected))
        advanceUntilIdle()

        assertEquals(expected, savedStateHandle[EXPENSES_FILTER_SAVED_STATE_KEY])
    }

    @Test
    fun `setSorting updates saved state handle`() = runTest {
        viewModel = createViewModel()

        val expected = ExpensesSortType.BY_MIN

        viewModel.onIntent(ExpensesIntent.SetSorting(expected))
        advanceUntilIdle()

        assertEquals(expected, savedStateHandle[EXPENSES_SORT_SAVED_STATE_KEY])
    }

    private fun createViewModel() = ExpensesViewModel(
        fakeObserveExpensesUiStateUseCase,
        savedStateHandle
    )
}