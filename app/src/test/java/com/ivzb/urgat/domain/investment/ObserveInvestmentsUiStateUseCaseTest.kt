package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.R
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentAsset
import com.ivzb.urgat.data.investment.InvestmentAverage
import com.ivzb.urgat.data.investment.InvestmentCategory
import com.ivzb.urgat.data.investment.InvestmentNetWorth
import com.ivzb.urgat.data.investment.InvestmentTotal
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.ui.investments.InvestmentsFilterType_All
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.calculatePnL
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveInvestmentsUiStateUseCaseTest {

    private lateinit var fakeObserveGroupedInvestmentsUseCase: FakeObserveGroupedInvestmentsMonthlyUseCase
    private lateinit var fakeObserveInvestmentAssetsUseCase: FakeObserveInvestmentAssetsUseCase
    private lateinit var fakeObserveGroupedInvestmentCategoriesYearlyUseCase: FakeObserveGroupedInvestmentCategoriesYearlyUseCase
    private lateinit var fakeObserveInvestmentCategoriesUseCase: FakeObserveInvestmentCategoriesUseCase
    private lateinit var fakeObserveInvestmentNetWorthUseCase: FakeObserveInvestmentNetWorthUseCase
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
        fakeObserveInvestmentsFilteringUseCase = FakeObserveInvestmentsFilteringUseCase()
        fakeObserveInvestmentsSortingUseCase = FakeObserveInvestmentsSortingUseCase()

        useCase = DefaultObserveInvestmentsUiStateUseCase(
            fakeObserveGroupedInvestmentsUseCase,
            fakeObserveGroupedInvestmentCategoriesYearlyUseCase,
            fakeObserveInvestmentAssetsUseCase,
            fakeObserveInvestmentCategoriesUseCase,
            fakeObserveInvestmentNetWorthUseCase,
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
        assertEquals(fakeInvestmentsFiltering, uiState.filteringUiInfo.filterMenuItems)
        assertTrue(uiState.filteringUiInfo.showFilterBadge)
        assertEquals(fakeInvestmentsSorting, uiState.filteringUiInfo.sortMenuItems)
        assertTrue(uiState.filteringUiInfo.showSortBadge)
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `emits averages when investments span multiple months`() = runTest {
        emitSuccessData(categories = fakeCategoriesMultipleMonths)

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(InvestmentsFilterType_All)
        val sortTypeFlow = flowOf(InvestmentsSortType.BY_DATE)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertEquals(
            mapOf(
                "2025" to expectedAverage2025,
                "2024" to InvestmentAverage.EMPTY,
            ),
            uiState.averagesYearly,
        )
        assertEquals(expectedAverageAllTime, uiState.averageAllTime)
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `emits no averages when investments span a single month`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(InvestmentsFilterType_All)
        val sortTypeFlow = flowOf(InvestmentsSortType.BY_DATE)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        // averaging a single month is the same as its total
        assertEquals(listOf(InvestmentAverage.EMPTY), uiState.averagesYearly.values.toList())
        assertEquals(InvestmentAverage.EMPTY, uiState.averageAllTime)
    }

    @Test
    fun `emits no averages when use case returns empty data`() = runTest {
        emitSuccessData(categories = listOf())

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(InvestmentsFilterType_All)
        val sortTypeFlow = flowOf(InvestmentsSortType.BY_DATE)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.averagesYearly.isEmpty())
        assertEquals(InvestmentAverage.EMPTY, uiState.averageAllTime)
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveGroupedInvestmentsUseCase.emit(Async.Error)
        fakeObserveGroupedInvestmentCategoriesYearlyUseCase.emit(Async.Error)
        fakeObserveInvestmentAssetsUseCase.emit(Async.Error)
        fakeObserveInvestmentCategoriesUseCase.emit(Async.Error)
        fakeObserveInvestmentNetWorthUseCase.emit(Async.Error)
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

    private fun emitSuccessData(categories: List<InvestmentCategory> = fakeCategories) {
        fakeObserveGroupedInvestmentsUseCase.emit(Async.Success(fakeInvestments))
        fakeObserveGroupedInvestmentCategoriesYearlyUseCase.emit(Async.Success(fakeCategoriesYearly))
        fakeObserveInvestmentAssetsUseCase.emit(Async.Success(fakeAssets))
        fakeObserveInvestmentCategoriesUseCase.emit(Async.Success(categories))
        fakeObserveInvestmentNetWorthUseCase.emit(Async.Success(fakeNetWorth))
        fakeObserveInvestmentsFilteringUseCase.emit(Async.Success(fakeInvestmentsFiltering))
        fakeObserveInvestmentsSortingUseCase.emit(Async.Success(fakeInvestmentsSorting))
    }

    companion object {

        private val investments = listOf(
            Investment("1", "SNP 500", 500.toBigDecimal(), 300.toBigDecimal(), 0.toBigDecimal(), "etfs", "USD", 1L, false, 1L)
        )

        private val fakeInvestments = listOf(
            GroupedItems(
                group = "2025/12",
                total = listOf(
                    InvestmentTotal(currency = "USD", costPrice = BigDecimal("150000"), marketPrice = BigDecimal("0"), pnl = BigDecimal("-100")),
                ),
                items = investments,
            ),
        )

        private val fakeCategoriesYearly = listOf(
            GroupedItems(
                group = "2025/12",
                total = listOf(
                    InvestmentTotal(currency = "USD", costPrice = BigDecimal("150000"), marketPrice = BigDecimal("0"), pnl = BigDecimal("-100")),
                ),
                items = listOf(
                    InvestmentCategory(
                        id = "1",
                        name = "etfs",
                        totals = listOf(
                            InvestmentTotal(currency = "USD", costPrice = BigDecimal("150000"), marketPrice = BigDecimal("0"), pnl = BigDecimal("-100")),
                        ),
                        investments = investments,
                    ),
                ),
            ),
        )

        private val fakeNetWorth = InvestmentNetWorth(
            totals = listOf(
                InvestmentTotal(currency = "USD", costPrice = BigDecimal("150000"), marketPrice = BigDecimal("0"), pnl = BigDecimal("-100")),
            ),
        )

        private val fakeCategories = listOf(
            InvestmentCategory(
                id = "1",
                name = "etfs",
                totals = listOf(
                    InvestmentTotal(currency = "USD", costPrice = BigDecimal("150000"), marketPrice = BigDecimal("0"), pnl = BigDecimal("-100")),
                ),
                investments = investments,
            ),
        )

        private val fakeAssets = listOf(
            InvestmentAsset(
                id = "1", name = "SNP 500", category = "etfs",
                shares = 500.toBigDecimal(), costPrice = 500.toBigDecimal(), marketPrice = 600.toBigDecimal(), currency = "USD",
                totals = listOf(
                    InvestmentTotal(currency = "EUR", costPrice = BigDecimal("1750.59"), marketPrice = BigDecimal("1750.59"), pnl = BigDecimal("0")),
                    InvestmentTotal(currency = "USD", costPrice = BigDecimal("2891.05"), marketPrice = BigDecimal("2891.05"), pnl = BigDecimal("0")),
                ),
                investments = investments,
            ),
        )

        /**
         * "stocks" spans over November and December 2025, "etf" over December 2025
         * and "gold" over December 2024 - 3 months in total.
         */
        private val fakeCategoriesMultipleMonths = listOf(
            InvestmentCategory(
                id = "1", name = "stocks",
                totals = listOf(investmentTotal("300", "330")),
                investments = listOf(
                    Investment("1", "GOOGL", BigDecimal("1"), BigDecimal("100"), BigDecimal("110"), "stocks", "USD", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25)),
                    Investment("2", "AAPL", BigDecimal("1"), BigDecimal("200"), BigDecimal("220"), "stocks", "USD", timestamp(2025, 12, 9), false, timestamp(2025, 12, 9)),
                ),
            ),
            InvestmentCategory(
                id = "2", name = "etf",
                totals = listOf(investmentTotal("400", "440")),
                investments = listOf(
                    Investment("3", "SNP 500", BigDecimal("1"), BigDecimal("400"), BigDecimal("440"), "etf", "USD", timestamp(2025, 12, 17), false, timestamp(2025, 12, 17)),
                ),
            ),
            InvestmentCategory(
                id = "3", name = "gold",
                totals = listOf(investmentTotal("60", "66")),
                investments = listOf(
                    Investment("4", "Gold", BigDecimal("1"), BigDecimal("60"), BigDecimal("66"), "gold", "USD", timestamp(2024, 12, 17), false, timestamp(2024, 12, 17)),
                ),
            ),
        )

        // 700 over the 2 months of 2025
        private val expectedAverage2025 = InvestmentAverage(
            totals = listOf(investmentTotal("350.00", "385.00")),
            categories = mapOf(
                "stocks" to listOf(investmentTotal("150.00", "165.00")),
                "etf" to listOf(investmentTotal("200.00", "220.00")),
            ),
            assets = mapOf(
                "GOOGL" to listOf(investmentTotal("50.00", "55.00")),
                "AAPL" to listOf(investmentTotal("100.00", "110.00")),
                "SNP 500" to listOf(investmentTotal("200.00", "220.00")),
            ),
        )

        // 760 over 3 months
        private val expectedAverageAllTime = InvestmentAverage(
            totals = listOf(investmentTotal("253.33", "278.67")),
            categories = mapOf(
                "stocks" to listOf(investmentTotal("100.00", "110.00")),
                "etf" to listOf(investmentTotal("133.33", "146.67")),
                "gold" to listOf(investmentTotal("20.00", "22.00")),
            ),
            assets = mapOf(
                "GOOGL" to listOf(investmentTotal("33.33", "36.67")),
                "AAPL" to listOf(investmentTotal("66.67", "73.33")),
                "SNP 500" to listOf(investmentTotal("133.33", "146.67")),
                "Gold" to listOf(investmentTotal("20.00", "22.00")),
            ),
        )

        private fun investmentTotal(costPrice: String, marketPrice: String, currency: String = "USD") =
            InvestmentTotal(
                currency = currency,
                costPrice = BigDecimal(costPrice),
                marketPrice = BigDecimal(marketPrice),
                pnl = calculatePnL(BigDecimal(costPrice), BigDecimal(marketPrice)),
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
