package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.R
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpenseAverage
import com.ivzb.urgat.data.expense.ExpenseCategory
import com.ivzb.urgat.data.expense.ExpenseTotal
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.ui.expenses.EXPENSE_PAGES
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesSortType
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.timestamp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
        assertTrue(uiState.averagesMonthly.isEmpty())
        assertTrue(uiState.averagesYearly.isEmpty())
        assertEquals(ExpenseAverage.EMPTY, uiState.averageAllTime)
        assertEquals(EXPENSE_PAGES, uiState.pages)
        assertEquals(0, uiState.initialPage)
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(ExpensesFilterType.ALL_EXPENSES)
        val sortTypeFlow = flowOf(ExpensesSortType.BY_DATE)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertEquals(fakeExpenses, uiState.expenses)
        assertEquals(fakeCategoriesMonthly, uiState.categoriesMonthly)
        assertEquals(fakeCategoriesYearly, uiState.categoriesYearly)
        assertEquals(fakeCategoriesAllTime, uiState.categoriesAllTime)
        assertEquals(fakeExpensesFiltering, uiState.filteringUiInfo.filterMenuItems)
        assertFalse(uiState.filteringUiInfo.showFilterBadge)
        assertEquals(fakeExpensesSorting, uiState.filteringUiInfo.sortMenuItems)
        assertFalse(uiState.filteringUiInfo.showSortBadge)
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `emits success state when use case returns data with badges`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(ExpensesFilterType.DEBIT_EXPENSES)
        val sortTypeFlow = flowOf(ExpensesSortType.BY_MAX)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertEquals(fakeExpenses, uiState.expenses)
        assertEquals(fakeCategoriesMonthly, uiState.categoriesMonthly)
        assertEquals(fakeCategoriesYearly, uiState.categoriesYearly)
        assertEquals(fakeCategoriesAllTime, uiState.categoriesAllTime)
        assertEquals(fakeExpensesFiltering, uiState.filteringUiInfo.filterMenuItems)
        assertTrue(uiState.filteringUiInfo.showFilterBadge)
        assertEquals(fakeExpensesSorting, uiState.filteringUiInfo.sortMenuItems)
        assertTrue(uiState.filteringUiInfo.showSortBadge)
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `emits averages when expenses span multiple months`() = runTest {
        emitSuccessData(categoriesAllTime = fakeCategoriesMultipleMonths)

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(ExpensesFilterType.ALL_EXPENSES)
        val sortTypeFlow = flowOf(ExpensesSortType.BY_DATE)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertEquals(
            mapOf(
                "November 2025" to expectedAverage2025,
                "December 2025" to expectedAverage2025,
                "December 2024" to ExpenseAverage.EMPTY,
            ),
            uiState.averagesMonthly,
        )
        assertEquals(
            mapOf(
                "2025" to expectedAverage2025,
                "2024" to ExpenseAverage.EMPTY,
            ),
            uiState.averagesYearly,
        )
        assertEquals(expectedAverageAllTime, uiState.averageAllTime)
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `emits no averages when expenses span a single month`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(ExpensesFilterType.ALL_EXPENSES)
        val sortTypeFlow = flowOf(ExpensesSortType.BY_DATE)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        // averaging a single month is the same as its total
        assertEquals(mapOf("December 2025" to ExpenseAverage.EMPTY), uiState.averagesMonthly)
        assertEquals(mapOf("2025" to ExpenseAverage.EMPTY), uiState.averagesYearly)
        assertEquals(ExpenseAverage.EMPTY, uiState.averageAllTime)
    }

    @Test
    fun `emits no averages when use case returns empty data`() = runTest {
        emitSuccessData(categoriesAllTime = listOf())

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(ExpensesFilterType.ALL_EXPENSES)
        val sortTypeFlow = flowOf(ExpensesSortType.BY_DATE)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.averagesMonthly.isEmpty())
        assertTrue(uiState.averagesYearly.isEmpty())
        assertEquals(ExpenseAverage.EMPTY, uiState.averageAllTime)
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

    @Test
    fun `hasAppliedFilters is false when no filters are applied`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(ExpensesFilterType.ALL_EXPENSES)
        val sortTypeFlow = flowOf(ExpensesSortType.BY_DATE)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertFalse(uiState.filteringUiInfo.hasAppliedFilters)
        assertEquals("", uiState.filteringUiInfo.searchQuery)
        assertFalse(uiState.filteringUiInfo.showFilterBadge)
        assertFalse(uiState.filteringUiInfo.showSortBadge)
        assertEquals(R.string.no_expenses, uiState.filteringUiInfo.emptyLabel)
        assertEquals(R.string.no_expenses_message, uiState.filteringUiInfo.emptyMessage)
        assertEquals(R.string.add_expense, uiState.filteringUiInfo.emptyButtonLabel)
    }

    @Test
    fun `hasAppliedFilters is true when search query is not empty`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("lunch")
        val filterTypeFlow = flowOf(ExpensesFilterType.ALL_EXPENSES)
        val sortTypeFlow = flowOf(ExpensesSortType.BY_DATE)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.filteringUiInfo.hasAppliedFilters)
        assertEquals("lunch", uiState.filteringUiInfo.searchQuery)
        assertFalse(uiState.filteringUiInfo.showFilterBadge)
        assertFalse(uiState.filteringUiInfo.showSortBadge)
        assertEquals(R.string.no_expenses_search, uiState.filteringUiInfo.emptyLabel)
        assertEquals(R.string.no_expenses_search_message, uiState.filteringUiInfo.emptyMessage)
        assertEquals(R.string.reset_filters, uiState.filteringUiInfo.emptyButtonLabel)
    }

    @Test
    fun `hasAppliedFilters is true when filter type is applied`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(ExpensesFilterType.DEBIT_EXPENSES)
        val sortTypeFlow = flowOf(ExpensesSortType.BY_DATE)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.filteringUiInfo.hasAppliedFilters)
        assertEquals("", uiState.filteringUiInfo.searchQuery)
        assertTrue(uiState.filteringUiInfo.showFilterBadge)
        assertFalse(uiState.filteringUiInfo.showSortBadge)
        assertEquals(R.string.no_expenses_search, uiState.filteringUiInfo.emptyLabel)
        assertEquals(R.string.no_expenses_search_message, uiState.filteringUiInfo.emptyMessage)
        assertEquals(R.string.reset_filters, uiState.filteringUiInfo.emptyButtonLabel)
    }

    @Test
    fun `hasAppliedFilters is true when sort type is applied`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(ExpensesFilterType.ALL_EXPENSES)
        val sortTypeFlow = flowOf(ExpensesSortType.BY_MAX)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.filteringUiInfo.hasAppliedFilters)
        assertEquals("", uiState.filteringUiInfo.searchQuery)
        assertFalse(uiState.filteringUiInfo.showFilterBadge)
        assertTrue(uiState.filteringUiInfo.showSortBadge)
        assertEquals(R.string.no_expenses_search, uiState.filteringUiInfo.emptyLabel)
        assertEquals(R.string.no_expenses_search_message, uiState.filteringUiInfo.emptyMessage)
        assertEquals(R.string.reset_filters, uiState.filteringUiInfo.emptyButtonLabel)
    }

    @Test
    fun `hasAppliedFilters is true when all filters are applied`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("lunch")
        val filterTypeFlow = flowOf(ExpensesFilterType.COST_EXPENSES)
        val sortTypeFlow = flowOf(ExpensesSortType.BY_MIN)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.filteringUiInfo.hasAppliedFilters)
        assertEquals("lunch", uiState.filteringUiInfo.searchQuery)
        assertTrue(uiState.filteringUiInfo.showFilterBadge)
        assertTrue(uiState.filteringUiInfo.showSortBadge)
        assertEquals(R.string.no_expenses_search, uiState.filteringUiInfo.emptyLabel)
        assertEquals(R.string.no_expenses_search_message, uiState.filteringUiInfo.emptyMessage)
        assertEquals(R.string.reset_filters, uiState.filteringUiInfo.emptyButtonLabel)
    }

    private fun emitSuccessData(categoriesAllTime: List<ExpenseCategory> = fakeCategoriesAllTime) {
        fakeObserveExpensesUseCase.emit(Async.Success(fakeExpenses))
        fakeObserveGroupedExpenseCategoriesMonthlyUseCase.emit(Async.Success(fakeCategoriesMonthly))
        fakeObserveGroupedExpenseCategoriesYearlyUseCase.emit(Async.Success(fakeCategoriesYearly))
        fakeObserveGroupedExpenseCategoriesAllTimeUseCase.emit(Async.Success(categoriesAllTime))
        fakeObserveExpensesFilteringUseCase.emit(Async.Success(fakeExpensesFiltering))
        fakeObserveExpensesSortingUseCase.emit(Async.Success(fakeExpensesSorting))
    }

    companion object {

        private val expenses = listOf(
            Expense("1", "Lunch", BigDecimal.TEN, "EUR", "food", timestamp(2025, 12, 20), false, false, timestamp(2025, 12, 20))
        )

        private val fakeExpenses = listOf(
            GroupedItems(group = "2025/12", total = listOf(ExpenseTotal(BigDecimal("10"), "EUR")), items = expenses)
        )

        private val fakeCategoriesMonthly = listOf(
            GroupedItems(
                group = "2025/12", total = listOf(ExpenseTotal(BigDecimal("10"), "EUR")),
                items = listOf(ExpenseCategory(id = "1", name = "food", totals = listOf(ExpenseTotal(BigDecimal("10"), "EUR")), expenses = expenses))
            )
        )

        private val fakeCategoriesYearly = listOf(
            GroupedItems(
                group = "2025", total = listOf(ExpenseTotal(BigDecimal("10"), "EUR")),
                items = listOf(ExpenseCategory(id = "1", name = "food", totals = listOf(ExpenseTotal(BigDecimal("10"), "EUR")), expenses = expenses))
            )
        )

        private val fakeCategoriesAllTime = listOf(
            ExpenseCategory(id = "1", name = "food", totals = listOf(ExpenseTotal(BigDecimal("10"), "EUR")), expenses = expenses)
        )

        /**
         * "food" spans over November and December 2025, "shopping" over December 2025
         * and "car" over December 2024 - 3 months in total.
         */
        private val fakeCategoriesMultipleMonths = listOf(
            ExpenseCategory(
                id = "1", name = "food", totals = listOf(ExpenseTotal(BigDecimal("150"), "EUR")),
                expenses = listOf(
                    Expense("1", "Lunch", BigDecimal("100"), "EUR", "food", timestamp(2025, 11, 25), false, false, timestamp(2025, 11, 25)),
                    Expense("2", "Dinner", BigDecimal("50"), "EUR", "food", timestamp(2025, 12, 9), false, false, timestamp(2025, 12, 9)),
                )
            ),
            ExpenseCategory(
                id = "2", name = "shopping", totals = listOf(ExpenseTotal(BigDecimal("150"), "EUR")),
                expenses = listOf(
                    Expense("3", "Clothing", BigDecimal("150"), "EUR", "shopping", timestamp(2025, 12, 20), false, false, timestamp(2025, 12, 20)),
                )
            ),
            ExpenseCategory(
                id = "3", name = "car", totals = listOf(ExpenseTotal(BigDecimal("60"), "EUR")),
                expenses = listOf(
                    Expense("4", "Gas", BigDecimal("60"), "EUR", "car", timestamp(2024, 12, 17), false, false, timestamp(2024, 12, 17)),
                )
            ),
        )

        private val expectedAverage2025 = ExpenseAverage(
            totals = listOf(ExpenseTotal(BigDecimal("150.00"), "EUR")),
            categories = mapOf(
                "food" to listOf(ExpenseTotal(BigDecimal("75.00"), "EUR")),
                "shopping" to listOf(ExpenseTotal(BigDecimal("75.00"), "EUR")),
            )
        )

        private val expectedAverageAllTime = ExpenseAverage(
            totals = listOf(ExpenseTotal(BigDecimal("120.00"), "EUR")),
            categories = mapOf(
                "food" to listOf(ExpenseTotal(BigDecimal("50.00"), "EUR")),
                "shopping" to listOf(ExpenseTotal(BigDecimal("50.00"), "EUR")),
                "car" to listOf(ExpenseTotal(BigDecimal("20.00"), "EUR")),
            )
        )

        private val fakeExpensesFiltering = listOf(
            FilterMenuItem(type = ExpensesFilterType.ALL_EXPENSES, labelResId = R.string.filter_all, size = 1),
            FilterMenuItem(type = ExpensesFilterType.DEBIT_EXPENSES, labelResId = R.string.filter_debit, size = 1),
            FilterMenuItem(type = ExpensesFilterType.COST_EXPENSES, labelResId = R.string.filter_cost, size = 0),
            FilterMenuItem(type = ExpensesFilterType.EXCLUDED_FROM_ANALYTICS, labelResId = R.string.filter_excluded_from_analytics, size = 0),
        )

        private val fakeExpensesSorting = listOf(
            SortMenuItem(type = ExpensesSortType.BY_DATE, labelResId = R.string.sort_by_date),
            SortMenuItem(type = ExpensesSortType.BY_MIN, labelResId = R.string.sort_by_min),
            SortMenuItem(type = ExpensesSortType.BY_MAX, labelResId = R.string.sort_by_max),
        )

    }

}
