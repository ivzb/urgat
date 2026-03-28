package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.R
import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.budget.BudgetCategory
import com.ivzb.urgat.data.budget.GroupedBudget
import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveBudgetsUiStateUseCaseTest {

    private lateinit var fakeObserveBudgetsUseCase: FakeObserveBudgetsUseCase
    private lateinit var fakeObserveGroupedBudgetsMonthlyUseCase: FakeObserveGroupedBudgetsMonthlyUseCase
    private lateinit var fakeObserveGroupedBudgetsYearlyUseCase: FakeObserveGroupedBudgetsYearlyUseCase
    private lateinit var fakeObserveGroupedBudgetsAllTimeUseCase: FakeObserveGroupedBudgetsAllTimeUseCase
    private lateinit var fakeObserveBudgetsFilteringUseCase: FakeObserveBudgetsFilteringUseCase
    private lateinit var fakeObserveBudgetsSortingUseCase: FakeObserveBudgetsSortingUseCase

    private lateinit var useCase: ObserveBudgetsUiStateUseCase

    @Before
    fun setUp() {
        fakeObserveBudgetsUseCase = FakeObserveBudgetsUseCase()
        fakeObserveGroupedBudgetsMonthlyUseCase = FakeObserveGroupedBudgetsMonthlyUseCase()
        fakeObserveGroupedBudgetsYearlyUseCase = FakeObserveGroupedBudgetsYearlyUseCase()
        fakeObserveGroupedBudgetsAllTimeUseCase = FakeObserveGroupedBudgetsAllTimeUseCase()
        fakeObserveBudgetsFilteringUseCase = FakeObserveBudgetsFilteringUseCase()
        fakeObserveBudgetsSortingUseCase = FakeObserveBudgetsSortingUseCase()

        useCase = DefaultObserveBudgetsUiStateUseCase(
            fakeObserveBudgetsUseCase,
            fakeObserveGroupedBudgetsMonthlyUseCase,
            fakeObserveGroupedBudgetsYearlyUseCase,
            fakeObserveGroupedBudgetsAllTimeUseCase,
            fakeObserveBudgetsFilteringUseCase,
            fakeObserveBudgetsSortingUseCase,
        )
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveBudgetsUseCase.emit(Async.Loading)
        fakeObserveGroupedBudgetsMonthlyUseCase.emit(Async.Loading)
        fakeObserveGroupedBudgetsYearlyUseCase.emit(Async.Loading)
        fakeObserveGroupedBudgetsAllTimeUseCase.emit(Async.Loading)
        fakeObserveBudgetsFilteringUseCase.emit(Async.Loading)
        fakeObserveBudgetsSortingUseCase.emit(Async.Loading)

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(BudgetsFilterType.ALL_BUDGETS)
        val sortTypeFlow = flowOf(BudgetsSortType.BY_DATE)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.isLoading)
        assertFalse(uiState.hasError)
        assertTrue(uiState.budgets.isEmpty())
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(BudgetsFilterType.ALL_BUDGETS)
        val sortTypeFlow = flowOf(BudgetsSortType.BY_DATE)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertEquals(fakeBudgets, uiState.budgets)
        assertEquals(fakeMonthlyBudgets, uiState.monthlyBudgets)
        assertEquals(fakeYearlyBudgets, uiState.yearlyBudgets)
        assertEquals(fakeAllTimeBudgets, uiState.allTimeBudget)
        assertEquals(fakeBudgetsFiltering, uiState.filteringUiInfo.filterMenuItems)
        assertFalse(uiState.filteringUiInfo.showFilterBadge)
        assertEquals(fakeBudgetsSorting, uiState.filteringUiInfo.sortMenuItems)
        assertFalse(uiState.filteringUiInfo.showSortBadge)
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `emits success state when use case returns data with badges`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(BudgetsFilterType.POSITIVE_BUDGETS)
        val sortTypeFlow = flowOf(BudgetsSortType.BY_MAX)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertEquals(fakeBudgets, uiState.budgets)
        assertEquals(fakeMonthlyBudgets, uiState.monthlyBudgets)
        assertEquals(fakeYearlyBudgets, uiState.yearlyBudgets)
        assertEquals(fakeAllTimeBudgets, uiState.allTimeBudget)
        assertEquals(fakeBudgetsFiltering, uiState.filteringUiInfo.filterMenuItems)
        assertTrue(uiState.filteringUiInfo.showFilterBadge)
        assertEquals(fakeBudgetsSorting, uiState.filteringUiInfo.sortMenuItems)
        assertTrue(uiState.filteringUiInfo.showSortBadge)
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
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
                    BudgetCategory(id = "1", name = "1", currency = "EUR", value = 24_000.toBigDecimal())
                ),
            ),
            totalIncome = 24_000.toBigDecimal(),
            totalExpenses = 7_600.toBigDecimal(),
            totalInvestments = 11_200.toBigDecimal(),
            totalSavings = 500.toBigDecimal(),
        )))
        fakeObserveBudgetsFilteringUseCase.emit(Async.Success(listOf()))
        fakeObserveBudgetsSortingUseCase.emit(Async.Success(listOf()))

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(BudgetsFilterType.ALL_BUDGETS)
        val sortTypeFlow = flowOf(BudgetsSortType.BY_DATE)

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
        val filterTypeFlow = flowOf(BudgetsFilterType.ALL_BUDGETS)
        val sortTypeFlow = flowOf(BudgetsSortType.BY_DATE)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertFalse(uiState.filteringUiInfo.hasAppliedFilters)
        assertEquals("", uiState.filteringUiInfo.searchQuery)
        assertFalse(uiState.filteringUiInfo.showFilterBadge)
        assertFalse(uiState.filteringUiInfo.showSortBadge)
        assertEquals(R.string.no_budgets, uiState.filteringUiInfo.emptyLabel)
        assertEquals(R.string.no_budgets_message, uiState.filteringUiInfo.emptyMessage)
        assertEquals(R.string.add_budget, uiState.filteringUiInfo.emptyButtonLabel)
    }

    @Test
    fun `hasAppliedFilters is true when search query is not empty`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("rent")
        val filterTypeFlow = flowOf(BudgetsFilterType.ALL_BUDGETS)
        val sortTypeFlow = flowOf(BudgetsSortType.BY_DATE)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.filteringUiInfo.hasAppliedFilters)
        assertEquals("rent", uiState.filteringUiInfo.searchQuery)
        assertFalse(uiState.filteringUiInfo.showFilterBadge)
        assertFalse(uiState.filteringUiInfo.showSortBadge)
        assertEquals(R.string.no_budgets_search, uiState.filteringUiInfo.emptyLabel)
        assertEquals(R.string.no_budgets_search_message, uiState.filteringUiInfo.emptyMessage)
        assertEquals(R.string.reset_filters, uiState.filteringUiInfo.emptyButtonLabel)
    }

    @Test
    fun `hasAppliedFilters is true when filter type is applied`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(BudgetsFilterType.POSITIVE_BUDGETS)
        val sortTypeFlow = flowOf(BudgetsSortType.BY_DATE)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.filteringUiInfo.hasAppliedFilters)
        assertEquals("", uiState.filteringUiInfo.searchQuery)
        assertTrue(uiState.filteringUiInfo.showFilterBadge)
        assertFalse(uiState.filteringUiInfo.showSortBadge)
        assertEquals(R.string.no_budgets_search, uiState.filteringUiInfo.emptyLabel)
        assertEquals(R.string.no_budgets_search_message, uiState.filteringUiInfo.emptyMessage)
        assertEquals(R.string.reset_filters, uiState.filteringUiInfo.emptyButtonLabel)
    }

    @Test
    fun `hasAppliedFilters is true when sort type is applied`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(BudgetsFilterType.ALL_BUDGETS)
        val sortTypeFlow = flowOf(BudgetsSortType.BY_MAX)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.filteringUiInfo.hasAppliedFilters)
        assertEquals("", uiState.filteringUiInfo.searchQuery)
        assertFalse(uiState.filteringUiInfo.showFilterBadge)
        assertTrue(uiState.filteringUiInfo.showSortBadge)
        assertEquals(R.string.no_budgets_search, uiState.filteringUiInfo.emptyLabel)
        assertEquals(R.string.no_budgets_search_message, uiState.filteringUiInfo.emptyMessage)
        assertEquals(R.string.reset_filters, uiState.filteringUiInfo.emptyButtonLabel)
    }

    @Test
    fun `hasAppliedFilters is true when all filters are applied`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("rent")
        val filterTypeFlow = flowOf(BudgetsFilterType.POSITIVE_BUDGETS)
        val sortTypeFlow = flowOf(BudgetsSortType.BY_MAX)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.filteringUiInfo.hasAppliedFilters)
        assertEquals("rent", uiState.filteringUiInfo.searchQuery)
        assertTrue(uiState.filteringUiInfo.showFilterBadge)
        assertTrue(uiState.filteringUiInfo.showSortBadge)
        assertEquals(R.string.no_budgets_search, uiState.filteringUiInfo.emptyLabel)
        assertEquals(R.string.no_budgets_search_message, uiState.filteringUiInfo.emptyMessage)
        assertEquals(R.string.reset_filters, uiState.filteringUiInfo.emptyButtonLabel)
    }

    private fun emitSuccessData() {
        fakeObserveBudgetsUseCase.emit(Async.Success(fakeBudgets))
        fakeObserveGroupedBudgetsMonthlyUseCase.emit(Async.Success(fakeMonthlyBudgets))
        fakeObserveGroupedBudgetsYearlyUseCase.emit(Async.Success(fakeYearlyBudgets))
        fakeObserveGroupedBudgetsAllTimeUseCase.emit(Async.Success(fakeAllTimeBudgets))
        fakeObserveBudgetsFilteringUseCase.emit(Async.Success(fakeBudgetsFiltering))
        fakeObserveBudgetsSortingUseCase.emit(Async.Success(fakeBudgetsSorting))
    }

    companion object {

        private val fakeBudgets = listOf(
            Budget("1", 6500.toBigDecimal(), 8800.toBigDecimal(), 7200.toBigDecimal(), "EUR", 1L, false, 1L),
            Budget("2", 5500.toBigDecimal(), 7800.toBigDecimal(), 6200.toBigDecimal(), "EUR", 1L, false, 1L),
        )

        private val fakeMonthlyBudgets = listOf(
            GroupedBudget(
                category = GroupedBudgetCategories(
                    group = "2025/12",
                    total = 6_500.toBigDecimal(),
                    items = listOf(BudgetCategory(id = "1", name = "1", currency = "EUR", value = 6_500.toBigDecimal())),
                ),
                totalIncome = 6_500.toBigDecimal(),
                totalExpenses = 2_000.toBigDecimal(),
                totalInvestments = 3_000.toBigDecimal(),
                totalSavings = 500.toBigDecimal(),
            ),
            GroupedBudget(
                category = GroupedBudgetCategories(
                    group = "2025/11",
                    total = 5_500.toBigDecimal(),
                    items = listOf(BudgetCategory(id = "1", name = "1", currency = "EUR", value = 5_500.toBigDecimal())),
                ),
                totalIncome = 5_500.toBigDecimal(),
                totalExpenses = 1_800.toBigDecimal(),
                totalInvestments = 2_600.toBigDecimal(),
                totalSavings = 500.toBigDecimal(),
            ),
        )

        private val fakeYearlyBudgets = listOf(
            GroupedBudget(
                category = GroupedBudgetCategories(
                    group = "2025",
                    total = 12_000.toBigDecimal(),
                    items = listOf(BudgetCategory(id = "1", name = "1", currency = "EUR", value = 12_000.toBigDecimal())),
                ),
                totalIncome = 12_000.toBigDecimal(),
                totalExpenses = 3_800.toBigDecimal(),
                totalInvestments = 5_600.toBigDecimal(),
                totalSavings = 500.toBigDecimal(),
            ),
        )

        private val fakeAllTimeBudgets = GroupedBudget(
            category = GroupedBudgetCategories(
                group = "All time",
                total = 24_000.toBigDecimal(),
                items = listOf(BudgetCategory(id = "1", name = "1", currency = "EUR", value = 24_000.toBigDecimal())),
            ),
            totalIncome = 24_000.toBigDecimal(),
            totalExpenses = 7_600.toBigDecimal(),
            totalInvestments = 11_200.toBigDecimal(),
            totalSavings = 500.toBigDecimal(),
        )

        private val fakeBudgetsFiltering = listOf(
            FilterMenuItem(type = BudgetsFilterType.ALL_BUDGETS, labelResId = R.string.filter_all, size = 1),
            FilterMenuItem(type = BudgetsFilterType.POSITIVE_BUDGETS, labelResId = R.string.filter_positive, size = 1),
            FilterMenuItem(type = BudgetsFilterType.NEGATIVE_BUDGETS, labelResId = R.string.filter_negative, size = 0),
        )

        private val fakeBudgetsSorting = listOf(
            SortMenuItem(type = BudgetsSortType.BY_DATE, labelResId = R.string.sort_by_date),
            SortMenuItem(type = BudgetsSortType.BY_MIN, labelResId = R.string.sort_by_min),
            SortMenuItem(type = BudgetsSortType.BY_MAX, labelResId = R.string.sort_by_max),
        )

    }

}
