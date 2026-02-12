package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.R
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpenseCategory
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveExpensesUiStateUseCaseTest {

    private lateinit var fakeObserveExpensesUseCase: FakeObserveGroupedExpensesMonthlyUseCase
    private lateinit var fakeObserveGroupedExpenseCategoriesMonthlyUseCase: FakeObserveGroupedExpenseCategoriesMonthlyUseCase
    private lateinit var fakeObserveGroupedExpenseCategoriesYearlyUseCase: FakeObserveGroupedExpenseCategoriesYearlyUseCase
    private lateinit var fakeObserveGroupedExpenseCategoriesAllTimeUseCase: FakeObserveGroupedExpenseCategoriesAllTimeUseCase
    private lateinit var fakeObserveExpensesFilteringUseCase: FakeObserveExpensesFilteringUseCase
    private lateinit var fakeObserveExpensesSortingUseCase: FakeObserveExpensesSortingUseCase

    private lateinit var useCase: ObserveExpensesUiStateUseCase

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        fakeObserveExpensesUseCase = FakeObserveGroupedExpensesMonthlyUseCase()
        fakeObserveGroupedExpenseCategoriesMonthlyUseCase = FakeObserveGroupedExpenseCategoriesMonthlyUseCase()
        fakeObserveGroupedExpenseCategoriesYearlyUseCase = FakeObserveGroupedExpenseCategoriesYearlyUseCase()
        fakeObserveGroupedExpenseCategoriesAllTimeUseCase = FakeObserveGroupedExpenseCategoriesAllTimeUseCase()
        fakeObserveExpensesFilteringUseCase = FakeObserveExpensesFilteringUseCase()
        fakeObserveExpensesSortingUseCase = FakeObserveExpensesSortingUseCase()

        useCase = DefaultObserveExpensesUiStateUseCase(
            fakeObserveExpensesUseCase,
            fakeObserveGroupedExpenseCategoriesMonthlyUseCase,
            fakeObserveGroupedExpenseCategoriesYearlyUseCase,
            fakeObserveGroupedExpenseCategoriesAllTimeUseCase,
            fakeObserveExpensesFilteringUseCase,
            fakeObserveExpensesSortingUseCase,
        )
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveExpensesUseCase.emit(Async.Loading)
        fakeObserveGroupedExpenseCategoriesMonthlyUseCase.emit(Async.Loading)
        fakeObserveGroupedExpenseCategoriesYearlyUseCase.emit(Async.Loading)
        fakeObserveGroupedExpenseCategoriesAllTimeUseCase.emit(Async.Loading)
        fakeObserveExpensesFilteringUseCase.emit(Async.Loading)
        fakeObserveExpensesSortingUseCase.emit(Async.Loading)

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(ExpensesFilterType.ALL_EXPENSES)
        val sortTypeFlow = flowOf(ExpensesSortType.BY_DATE)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.isLoading)
        assertFalse(uiState.hasError)
        assertTrue(uiState.expenses.isEmpty())
        assertTrue(uiState.categoriesMonthly.isEmpty())
        assertTrue(uiState.categoriesYearly.isEmpty())
        assertTrue(uiState.categoriesAllTime.isEmpty())
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

        fakeObserveExpensesUseCase.emit(Async.Success(fakeExpenses))
        fakeObserveGroupedExpenseCategoriesMonthlyUseCase.emit(Async.Success(fakeCategoriesMonthly))
        fakeObserveGroupedExpenseCategoriesYearlyUseCase.emit(Async.Success(fakeCategoriesYearly))
        fakeObserveGroupedExpenseCategoriesAllTimeUseCase.emit(Async.Success(fakeCategoriesAllTime))
        fakeObserveExpensesFilteringUseCase.emit(Async.Success(fakeExpensesFiltering))
        fakeObserveExpensesSortingUseCase.emit(Async.Success(fakeExpensesSorting))

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(ExpensesFilterType.ALL_EXPENSES)
        val sortTypeFlow = flowOf(ExpensesSortType.BY_DATE)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        Assert.assertEquals(fakeExpenses, uiState.expenses)
        Assert.assertEquals(fakeCategoriesMonthly, uiState.categoriesMonthly)
        Assert.assertEquals(fakeCategoriesYearly, uiState.categoriesYearly)
        Assert.assertEquals(fakeCategoriesAllTime, uiState.categoriesAllTime)
        Assert.assertEquals(fakeExpensesFiltering, uiState.filteringUiInfo.filterMenuItems)
        Assert.assertEquals(fakeExpensesSorting, uiState.filteringUiInfo.sortMenuItems)
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveExpensesUseCase.emit(Async.Error)
        fakeObserveGroupedExpenseCategoriesMonthlyUseCase.emit(Async.Error)
        fakeObserveGroupedExpenseCategoriesYearlyUseCase.emit(Async.Error)
        fakeObserveGroupedExpenseCategoriesAllTimeUseCase.emit(Async.Error)
        fakeObserveExpensesFilteringUseCase.emit(Async.Error)
        fakeObserveExpensesSortingUseCase.emit(Async.Error)

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(ExpensesFilterType.ALL_EXPENSES)
        val sortTypeFlow = flowOf(ExpensesSortType.BY_DATE)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.hasError)
        assertFalse(uiState.isLoading)
    }

}
