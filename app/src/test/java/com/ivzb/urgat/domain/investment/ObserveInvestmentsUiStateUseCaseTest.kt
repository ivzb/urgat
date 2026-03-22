package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.R
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentAsset
import com.ivzb.urgat.data.investment.InvestmentCategory
import com.ivzb.urgat.data.investment.InvestmentNetWorth
import com.ivzb.urgat.data.portfolio.Portfolio
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.ui.investments.InvestmentsFilterType_All
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveInvestmentsUiStateUseCaseTest {

    private lateinit var fakeObserveGroupedInvestmentsUseCase: FakeObserveGroupedInvestmentsMonthlyUseCase
    private lateinit var fakeObserveInvestmentAssetsUseCase: FakeObserveInvestmentAssetsUseCase
    private lateinit var fakeObserveGroupedInvestmentCategoriesYearlyUseCase: FakeObserveGroupedInvestmentCategoriesYearlyUseCase
    private lateinit var fakeObserveInvestmentCategoriesUseCase: FakeObserveInvestmentCategoriesUseCase
    private lateinit var fakeObserveInvestmentNetWorthUseCase: FakeObserveInvestmentNetWorthUseCase
    private lateinit var fakeObservePortfolioUseCase: FakeObservePortfolioUseCase
    private lateinit var fakeObserveInvestmentsFilteringUseCase: FakeObserveInvestmentsFilteringUseCase
    private lateinit var fakeObserveInvestmentsSortingUseCase: FakeObserveInvestmentsSortingUseCase

    private lateinit var useCase: ObserveInvestmentsUiStateUseCase

    @Before
    fun setUp() {
        fakeObserveGroupedInvestmentsUseCase = FakeObserveGroupedInvestmentsMonthlyUseCase()
        fakeObserveInvestmentAssetsUseCase = FakeObserveInvestmentAssetsUseCase()
        fakeObserveGroupedInvestmentCategoriesYearlyUseCase = FakeObserveGroupedInvestmentCategoriesYearlyUseCase()
        fakeObserveInvestmentCategoriesUseCase = FakeObserveInvestmentCategoriesUseCase()
        fakeObserveInvestmentNetWorthUseCase = FakeObserveInvestmentNetWorthUseCase()
        fakeObservePortfolioUseCase = FakeObservePortfolioUseCase()
        fakeObserveInvestmentsFilteringUseCase = FakeObserveInvestmentsFilteringUseCase()
        fakeObserveInvestmentsSortingUseCase = FakeObserveInvestmentsSortingUseCase()

        useCase = DefaultObserveInvestmentsUiStateUseCase(
            fakeObserveGroupedInvestmentsUseCase,
            fakeObserveGroupedInvestmentCategoriesYearlyUseCase,
            fakeObserveInvestmentAssetsUseCase,
            fakeObserveInvestmentCategoriesUseCase,
            fakeObserveInvestmentNetWorthUseCase,
            fakeObservePortfolioUseCase,
            fakeObserveInvestmentsFilteringUseCase,
            fakeObserveInvestmentsSortingUseCase,
        )
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveGroupedInvestmentsUseCase.emit(Async.Loading)
        fakeObserveGroupedInvestmentCategoriesYearlyUseCase.emit(Async.Loading)
        fakeObserveInvestmentAssetsUseCase.emit(Async.Loading)
        fakeObserveInvestmentCategoriesUseCase.emit(Async.Loading)
        fakeObserveInvestmentNetWorthUseCase.emit(Async.Loading)
        fakeObservePortfolioUseCase.emit(Async.Loading)
        fakeObserveInvestmentsFilteringUseCase.emit(Async.Loading)
        fakeObserveInvestmentsSortingUseCase.emit(Async.Loading)

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(InvestmentsFilterType_All)
        val sortTypeFlow = flowOf(InvestmentsSortType.BY_DATE)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.isLoading)
        assertFalse(uiState.hasError)
        assertTrue(uiState.investments.isEmpty())
        assertTrue(uiState.categoriesYearly.isEmpty())
        assertTrue(uiState.assets.isEmpty())
        assertTrue(uiState.categories.isEmpty())
        assertNull(uiState.netWorth)
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(InvestmentsFilterType_All)
        val sortTypeFlow = flowOf(InvestmentsSortType.BY_DATE)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertEquals(fakeInvestments, uiState.investments)
        assertEquals(fakeNetWorth, uiState.netWorth)
        assertEquals(fakeCategories, uiState.categories)
        assertEquals(fakeAssets, uiState.assets)
        assertEquals(fakeInvestmentsFiltering, uiState.filteringUiInfo.filterMenuItems)
        assertFalse(uiState.filteringUiInfo.showFilterBadge)
        assertEquals(fakeInvestmentsSorting, uiState.filteringUiInfo.sortMenuItems)
        assertFalse(uiState.filteringUiInfo.showSortBadge)
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `emits success state when use case returns data with badges`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf("stocks")
        val sortTypeFlow = flowOf(InvestmentsSortType.BY_MARKET_VALUE)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertEquals(fakeInvestments, uiState.investments)
        assertEquals(fakeNetWorth, uiState.netWorth)
        assertEquals(fakeCategories, uiState.categories)
        assertEquals(fakeAssets, uiState.assets)
        assertEquals(fakePortfolio, uiState.portfolio)
        assertEquals(fakeInvestmentsFiltering, uiState.filteringUiInfo.filterMenuItems)
        assertTrue(uiState.filteringUiInfo.showFilterBadge)
        assertEquals(fakeInvestmentsSorting, uiState.filteringUiInfo.sortMenuItems)
        assertTrue(uiState.filteringUiInfo.showSortBadge)
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveGroupedInvestmentsUseCase.emit(Async.Error)
        fakeObserveGroupedInvestmentCategoriesYearlyUseCase.emit(Async.Error)
        fakeObserveInvestmentAssetsUseCase.emit(Async.Error)
        fakeObserveInvestmentCategoriesUseCase.emit(Async.Error)
        fakeObserveInvestmentNetWorthUseCase.emit(Async.Error)
        fakeObservePortfolioUseCase.emit(Async.Error)
        fakeObserveInvestmentsFilteringUseCase.emit(Async.Success(listOf()))
        fakeObserveInvestmentsSortingUseCase.emit(Async.Success(listOf()))

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(InvestmentsFilterType_All)
        val sortTypeFlow = flowOf(InvestmentsSortType.BY_DATE)

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
        val filterTypeFlow = flowOf(InvestmentsFilterType_All)
        val sortTypeFlow = flowOf(InvestmentsSortType.BY_DATE)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertFalse(uiState.filteringUiInfo.hasAppliedFilters)
        assertEquals("", uiState.filteringUiInfo.searchQuery)
        assertFalse(uiState.filteringUiInfo.showFilterBadge)
        assertFalse(uiState.filteringUiInfo.showSortBadge)
        assertEquals(R.string.no_investments, uiState.filteringUiInfo.emptyLabel)
        assertEquals(R.string.no_investments_message, uiState.filteringUiInfo.emptyMessage)
        assertEquals(R.string.add_investment, uiState.filteringUiInfo.emptyButtonLabel)
    }

    @Test
    fun `hasAppliedFilters is true when search query is not empty`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("lunch")
        val filterTypeFlow = flowOf(InvestmentsFilterType_All)
        val sortTypeFlow = flowOf(InvestmentsSortType.BY_DATE)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.filteringUiInfo.hasAppliedFilters)
        assertEquals("lunch", uiState.filteringUiInfo.searchQuery)
        assertFalse(uiState.filteringUiInfo.showFilterBadge)
        assertFalse(uiState.filteringUiInfo.showSortBadge)
        assertEquals(R.string.no_investments_search, uiState.filteringUiInfo.emptyLabel)
        assertEquals(R.string.no_investments_search_message, uiState.filteringUiInfo.emptyMessage)
        assertEquals(R.string.reset_filters, uiState.filteringUiInfo.emptyButtonLabel)
    }

    @Test
    fun `hasAppliedFilters is true when filter type is applied`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf("stocks")
        val sortTypeFlow = flowOf(InvestmentsSortType.BY_DATE)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.filteringUiInfo.hasAppliedFilters)
        assertEquals("", uiState.filteringUiInfo.searchQuery)
        assertTrue(uiState.filteringUiInfo.showFilterBadge)
        assertFalse(uiState.filteringUiInfo.showSortBadge)
        assertEquals(R.string.no_investments_search, uiState.filteringUiInfo.emptyLabel)
        assertEquals(R.string.no_investments_search_message, uiState.filteringUiInfo.emptyMessage)
        assertEquals(R.string.reset_filters, uiState.filteringUiInfo.emptyButtonLabel)
    }

    @Test
    fun `hasAppliedFilters is true when sort type is applied`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(InvestmentsFilterType_All)
        val sortTypeFlow = flowOf(InvestmentsSortType.BY_MARKET_VALUE)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.filteringUiInfo.hasAppliedFilters)
        assertEquals("", uiState.filteringUiInfo.searchQuery)
        assertFalse(uiState.filteringUiInfo.showFilterBadge)
        assertTrue(uiState.filteringUiInfo.showSortBadge)
        assertEquals(R.string.no_investments_search, uiState.filteringUiInfo.emptyLabel)
        assertEquals(R.string.no_investments_search_message, uiState.filteringUiInfo.emptyMessage)
        assertEquals(R.string.reset_filters, uiState.filteringUiInfo.emptyButtonLabel)
    }

    @Test
    fun `hasAppliedFilters is true when all filters are applied`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("lunch")
        val filterTypeFlow = flowOf("stocks")
        val sortTypeFlow = flowOf(InvestmentsSortType.BY_MARKET_VALUE)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.filteringUiInfo.hasAppliedFilters)
        assertEquals("lunch", uiState.filteringUiInfo.searchQuery)
        assertTrue(uiState.filteringUiInfo.showFilterBadge)
        assertTrue(uiState.filteringUiInfo.showSortBadge)
        assertEquals(R.string.no_investments_search, uiState.filteringUiInfo.emptyLabel)
        assertEquals(R.string.no_investments_search_message, uiState.filteringUiInfo.emptyMessage)
        assertEquals(R.string.reset_filters, uiState.filteringUiInfo.emptyButtonLabel)
    }

    private fun emitSuccessData() {
        fakeObserveGroupedInvestmentsUseCase.emit(Async.Success(fakeInvestments))
        fakeObserveGroupedInvestmentCategoriesYearlyUseCase.emit(Async.Success(fakeCategoriesYearly))
        fakeObserveInvestmentAssetsUseCase.emit(Async.Success(fakeAssets))
        fakeObserveInvestmentCategoriesUseCase.emit(Async.Success(fakeCategories))
        fakeObserveInvestmentNetWorthUseCase.emit(Async.Success(fakeNetWorth))
        fakeObservePortfolioUseCase.emit(Async.Success(fakePortfolio))
        fakeObserveInvestmentsFilteringUseCase.emit(Async.Success(fakeInvestmentsFiltering))
        fakeObserveInvestmentsSortingUseCase.emit(Async.Success(fakeInvestmentsSorting))
    }

    companion object {

        private val investments = listOf(
            Investment("1", "SNP 500", 500.toBigDecimal(), 300.toBigDecimal(), "etfs", "USD", 1L, false, 1L)
        )

        private val fakeInvestments = listOf(
            GroupedItems("2025/12", "1500 USD", investments)
        )

        private val fakeCategoriesYearly = listOf(
            GroupedItems(
                group = "2025/12", total = "1500 USD",
                items = listOf(InvestmentCategory(id = "1", name = "etfs", total = "500 USD", investments = investments))
            )
        )

        private val fakeNetWorth = InvestmentNetWorth(total = "500 USD")

        private val fakeCategories = listOf(
            InvestmentCategory(id = "1", name = "etfs", total = "500 USD", investments = investments)
        )

        private val fakeAssets = listOf(
            InvestmentAsset(
                id = "1", name = "SNP 500", category = "etfs",
                shares = 500.toBigDecimal(), marketValue = 500.toBigDecimal(), currency = "USD",
                investments = investments, total = "1,750.59 EUR | 2,891.05 USD",
            )
        )

        private val fakePortfolio = listOf(
            Portfolio(
                name = "SNP 500",
                category = "etfs",
                shares = 500.toBigDecimal(),
                costValue = 500.toBigDecimal(),
                marketValue = 1000.toBigDecimal(),
                currency = "USD",
            )
        )

        private val fakeInvestmentsFiltering = listOf(
            FilterMenuItem(type = "stocks", label = "stocks", size = 1),
        )

        private val fakeInvestmentsSorting = listOf(
            SortMenuItem(type = InvestmentsSortType.BY_DATE, labelResId = R.string.sort_by_date),
            SortMenuItem(type = InvestmentsSortType.BY_MARKET_VALUE, labelResId = R.string.sort_by_market_value),
        )

    }

}
