package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.R
import com.ivzb.urgat.data.fuelConsumption.FuelConsumption
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionCategory
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionTotal
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.ui.fuelConsumption.FUEL_CONSUMPTION_PAGES
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionFilterType_All
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionSortType
import com.ivzb.urgat.util.Async
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
class ObserveFuelConsumptionUiStateUseCaseTest {

    private lateinit var fakeObserveGroupedFuelConsumptionMonthlyUseCase: FakeObserveGroupedFuelConsumptionMonthlyUseCase
    private lateinit var fakeObserveGroupedFuelConsumptionCategoriesMonthlyUseCase: FakeObserveGroupedFuelConsumptionCategoriesMonthlyUseCase
    private lateinit var fakeObserveGroupedFuelConsumptionCategoriesYearlyUseCase: FakeObserveGroupedFuelConsumptionCategoriesYearlyUseCase
    private lateinit var fakeObserveGroupedFuelConsumptionCategoriesAllTimeUseCase: FakeObserveGroupedFuelConsumptionCategoriesAllTimeUseCase
    private lateinit var fakeObserveFuelConsumptionFilteringUseCase: FakeObserveFuelConsumptionFilteringUseCase
    private lateinit var fakeObserveFuelConsumptionSortingUseCase: FakeObserveFuelConsumptionSortingUseCase

    private lateinit var useCase: ObserveFuelConsumptionUiStateUseCase

    @Before
    fun setUp() {
        fakeObserveGroupedFuelConsumptionMonthlyUseCase = FakeObserveGroupedFuelConsumptionMonthlyUseCase()
        fakeObserveGroupedFuelConsumptionCategoriesMonthlyUseCase = FakeObserveGroupedFuelConsumptionCategoriesMonthlyUseCase()
        fakeObserveGroupedFuelConsumptionCategoriesYearlyUseCase = FakeObserveGroupedFuelConsumptionCategoriesYearlyUseCase()
        fakeObserveGroupedFuelConsumptionCategoriesAllTimeUseCase = FakeObserveGroupedFuelConsumptionCategoriesAllTimeUseCase()
        fakeObserveFuelConsumptionFilteringUseCase = FakeObserveFuelConsumptionFilteringUseCase()
        fakeObserveFuelConsumptionSortingUseCase = FakeObserveFuelConsumptionSortingUseCase()

        useCase = DefaultObserveFuelConsumptionUiStateUseCase(
            fakeObserveGroupedFuelConsumptionMonthlyUseCase,
            fakeObserveGroupedFuelConsumptionCategoriesMonthlyUseCase,
            fakeObserveGroupedFuelConsumptionCategoriesYearlyUseCase,
            fakeObserveGroupedFuelConsumptionCategoriesAllTimeUseCase,
            fakeObserveFuelConsumptionFilteringUseCase,
            fakeObserveFuelConsumptionSortingUseCase,
        )
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveGroupedFuelConsumptionMonthlyUseCase.emit(Async.Loading)
        fakeObserveGroupedFuelConsumptionCategoriesMonthlyUseCase.emit(Async.Loading)
        fakeObserveGroupedFuelConsumptionCategoriesYearlyUseCase.emit(Async.Loading)
        fakeObserveGroupedFuelConsumptionCategoriesAllTimeUseCase.emit(Async.Loading)
        fakeObserveFuelConsumptionFilteringUseCase.emit(Async.Loading)
        fakeObserveFuelConsumptionSortingUseCase.emit(Async.Loading)

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(FuelConsumptionFilterType_All)
        val sortTypeFlow = flowOf(FuelConsumptionSortType.BY_DATE)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.isLoading)
        assertFalse(uiState.hasError)
        assertTrue(uiState.fuelConsumption.isEmpty())
        assertTrue(uiState.categoriesMonthly.isEmpty())
        assertTrue(uiState.categoriesYearly.isEmpty())
        assertTrue(uiState.categoriesAllTime.isEmpty())
        assertEquals(FUEL_CONSUMPTION_PAGES, uiState.pages)
        assertEquals(0, uiState.initialPage)
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(FuelConsumptionFilterType_All)
        val sortTypeFlow = flowOf(FuelConsumptionSortType.BY_DATE)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertEquals(fakeGroupedFuelConsumption, uiState.fuelConsumption)
        assertEquals(fakeCategoriesMonthly, uiState.categoriesMonthly)
        assertEquals(fakeCategoriesYearly, uiState.categoriesYearly)
        assertEquals(fakeCategoriesAllTime, uiState.categoriesAllTime)
        assertEquals(fakeFiltering, uiState.filteringUiInfo.filterMenuItems)
        assertFalse(uiState.filteringUiInfo.showFilterBadge)
        assertEquals(fakeSorting, uiState.filteringUiInfo.sortMenuItems)
        assertFalse(uiState.filteringUiInfo.showSortBadge)
        assertFalse(uiState.filteringUiInfo.hasAppliedFilters)
        assertEquals(R.string.no_fuel_consumption, uiState.filteringUiInfo.emptyLabel)
        assertEquals(R.string.no_fuel_consumption_message, uiState.filteringUiInfo.emptyMessage)
        assertEquals(R.string.add_expense, uiState.filteringUiInfo.emptyButtonLabel)
        assertEquals(FUEL_CONSUMPTION_PAGES, uiState.pages)
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `emits success state with filter and sort badges when non default values are used`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("diesel")
        val filterTypeFlow = flowOf("Diesel")
        val sortTypeFlow = flowOf(FuelConsumptionSortType.BY_MAX)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertEquals(fakeGroupedFuelConsumption, uiState.fuelConsumption)
        assertTrue(uiState.filteringUiInfo.hasAppliedFilters)
        assertEquals("diesel", uiState.filteringUiInfo.searchQuery)
        assertTrue(uiState.filteringUiInfo.showFilterBadge)
        assertTrue(uiState.filteringUiInfo.showSortBadge)
        assertEquals(R.string.no_fuel_consumption_search, uiState.filteringUiInfo.emptyLabel)
        assertEquals(R.string.no_fuel_consumption_search_message, uiState.filteringUiInfo.emptyMessage)
        assertEquals(R.string.reset_filters, uiState.filteringUiInfo.emptyButtonLabel)
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveGroupedFuelConsumptionMonthlyUseCase.emit(Async.Error)
        fakeObserveGroupedFuelConsumptionCategoriesMonthlyUseCase.emit(Async.Error)
        fakeObserveGroupedFuelConsumptionCategoriesYearlyUseCase.emit(Async.Error)
        fakeObserveGroupedFuelConsumptionCategoriesAllTimeUseCase.emit(Async.Error)
        fakeObserveFuelConsumptionFilteringUseCase.emit(Async.Error)
        fakeObserveFuelConsumptionSortingUseCase.emit(Async.Error)

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(FuelConsumptionFilterType_All)
        val sortTypeFlow = flowOf(FuelConsumptionSortType.BY_DATE)

        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.hasError)
        assertFalse(uiState.isLoading)
    }

    @Test
    fun `sort badge is false for BY_DATE`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(FuelConsumptionFilterType_All)
        val sortTypeFlow = flowOf(FuelConsumptionSortType.BY_DATE)
        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()
        advanceUntilIdle()

        assertTrue(results is Async.Success)
        assertFalse(results.data.filteringUiInfo.showSortBadge)
    }

    @Test
    fun `sort badge is true for BY_MIN`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(FuelConsumptionFilterType_All)
        val sortTypeFlow = flowOf(FuelConsumptionSortType.BY_MIN)
        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()
        advanceUntilIdle()

        assertTrue(results is Async.Success)
        assertTrue(results.data.filteringUiInfo.showSortBadge)
    }

    @Test
    fun `filter badge is false for all filter type`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(FuelConsumptionFilterType_All)
        val sortTypeFlow = flowOf(FuelConsumptionSortType.BY_DATE)
        val results = useCase(searchQueryFlow, filterTypeFlow, sortTypeFlow).first()
        advanceUntilIdle()

        assertTrue(results is Async.Success)
        assertFalse(results.data.filteringUiInfo.showFilterBadge)
        assertEquals(R.string.transactions, results.data.filteringUiInfo.currentFilteringLabel)
    }

    @Test
    fun `filter badge is true for specific fuel type`() = runTest {
        emitSuccessData()

        val results = useCase(
            searchQueryFlow = flowOf(""),
            filterTypeFlow = flowOf("Diesel"),
            sortTypeFlow = flowOf(FuelConsumptionSortType.BY_DATE),
        ).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        assertTrue(results.data.filteringUiInfo.showFilterBadge)
        assertTrue(results.data.filteringUiInfo.hasAppliedFilters)
    }

    private fun emitSuccessData() {
        fakeObserveGroupedFuelConsumptionMonthlyUseCase.emit(Async.Success(fakeGroupedFuelConsumption))
        fakeObserveGroupedFuelConsumptionCategoriesMonthlyUseCase.emit(Async.Success(fakeCategoriesMonthly))
        fakeObserveGroupedFuelConsumptionCategoriesYearlyUseCase.emit(Async.Success(fakeCategoriesYearly))
        fakeObserveGroupedFuelConsumptionCategoriesAllTimeUseCase.emit(Async.Success(fakeCategoriesAllTime))
        fakeObserveFuelConsumptionFilteringUseCase.emit(Async.Success(fakeFiltering))
        fakeObserveFuelConsumptionSortingUseCase.emit(Async.Success(fakeSorting))
    }

    companion object {

        private val fakeFuelConsumption = listOf(
            FuelConsumption("fc_1", BigDecimal("45.5"), BigDecimal("2.35"), "EUR", "Diesel", "Mercedes w140", 150000, 1000L, false, 1000L),
            FuelConsumption("fc_2", BigDecimal("40.0"), BigDecimal("2.40"), "EUR", "Diesel", "Mercedes w140", 149500, 2000L, false, 2000L),
        )

        private val fakeGroupedFuelConsumption = listOf(
            GroupedItems(
                group = "January 1970",
                total = listOf(FuelConsumptionTotal("Diesel", BigDecimal("85.5"))),
                items = fakeFuelConsumption,
            )
        )

        private val fakeCategoriesMonthly = listOf(
            GroupedItems(
                group = "January 1970",
                total = listOf(FuelConsumptionTotal("Diesel", BigDecimal("85.5"))),
                items = listOf(
                    FuelConsumptionCategory(
                        id = "1",
                        name = "Diesel",
                        totals = listOf(FuelConsumptionTotal("Diesel", BigDecimal("85.5"))),
                        fuelConsumption = fakeFuelConsumption,
                    )
                ),
            )
        )

        private val fakeCategoriesYearly = listOf(
            GroupedItems(
                group = "1970",
                total = listOf(FuelConsumptionTotal("Diesel", BigDecimal("85.5"))),
                items = listOf(
                    FuelConsumptionCategory(
                        id = "1",
                        name = "Diesel",
                        totals = listOf(FuelConsumptionTotal("Diesel", BigDecimal("85.5"))),
                        fuelConsumption = fakeFuelConsumption,
                    )
                ),
            )
        )

        private val fakeCategoriesAllTime = listOf(
            FuelConsumptionCategory(
                id = "1",
                name = "Diesel",
                totals = listOf(FuelConsumptionTotal("Diesel", BigDecimal("85.5"))),
                fuelConsumption = fakeFuelConsumption,
            )
        )

        private val fakeFiltering = listOf(
            FilterMenuItem(type = FuelConsumptionFilterType_All, labelResId = R.string.filter_all, size = 2),
            FilterMenuItem(type = "Diesel", label = "Diesel", size = 2),
        )

        private val fakeSorting = listOf(
            SortMenuItem(type = FuelConsumptionSortType.BY_DATE, labelResId = R.string.sort_by_date),
            SortMenuItem(type = FuelConsumptionSortType.BY_MIN, labelResId = R.string.sort_by_min),
            SortMenuItem(type = FuelConsumptionSortType.BY_MAX, labelResId = R.string.sort_by_max),
        )

    }

}

