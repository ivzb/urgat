package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.R
import com.ivzb.urgat.data.fuelConsumption.FuelConsumption
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionTotal
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.ui.components.SortMenuItem
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
    private lateinit var fakeObserveFuelConsumptionSortingUseCase: FakeObserveFuelConsumptionSortingUseCase

    private lateinit var useCase: ObserveFuelConsumptionUiStateUseCase

    @Before
    fun setUp() {
        fakeObserveGroupedFuelConsumptionMonthlyUseCase = FakeObserveGroupedFuelConsumptionMonthlyUseCase()
        fakeObserveFuelConsumptionSortingUseCase = FakeObserveFuelConsumptionSortingUseCase()

        useCase = DefaultObserveFuelConsumptionUiStateUseCase(
            fakeObserveGroupedFuelConsumptionMonthlyUseCase,
            fakeObserveFuelConsumptionSortingUseCase,
        )
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveGroupedFuelConsumptionMonthlyUseCase.emit(Async.Loading)
        fakeObserveFuelConsumptionSortingUseCase.emit(Async.Loading)

        val searchQueryFlow = flowOf("")
        val sortTypeFlow = flowOf(FuelConsumptionSortType.BY_DATE)

        val results = useCase(searchQueryFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.isLoading)
        assertFalse(uiState.hasError)
        assertTrue(uiState.fuelConsumption.isEmpty())
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val sortTypeFlow = flowOf(FuelConsumptionSortType.BY_DATE)

        val results = useCase(searchQueryFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertEquals(fakeGroupedFuelConsumption, uiState.fuelConsumption)
        assertEquals(fakeSorting, uiState.filteringUiInfo.sortMenuItems)
        assertFalse(uiState.filteringUiInfo.showSortBadge)
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `emits success state with sort badge when sort type is not BY_DATE`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val sortTypeFlow = flowOf(FuelConsumptionSortType.BY_MAX)

        val results = useCase(searchQueryFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertEquals(fakeGroupedFuelConsumption, uiState.fuelConsumption)
        assertTrue(uiState.filteringUiInfo.showSortBadge)
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveGroupedFuelConsumptionMonthlyUseCase.emit(Async.Error)
        fakeObserveFuelConsumptionSortingUseCase.emit(Async.Error)

        val searchQueryFlow = flowOf("")
        val sortTypeFlow = flowOf(FuelConsumptionSortType.BY_DATE)

        val results = useCase(searchQueryFlow, sortTypeFlow).first()

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
        val sortTypeFlow = flowOf(FuelConsumptionSortType.BY_DATE)
        val results = useCase(searchQueryFlow, sortTypeFlow).first()
        advanceUntilIdle()

        assertTrue(results is Async.Success)
        assertFalse(results.data.filteringUiInfo.showSortBadge)
    }

    @Test
    fun `sort badge is true for BY_MIN`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val sortTypeFlow = flowOf(FuelConsumptionSortType.BY_MIN)
        val results = useCase(searchQueryFlow, sortTypeFlow).first()
        advanceUntilIdle()

        assertTrue(results is Async.Success)
        assertTrue(results.data.filteringUiInfo.showSortBadge)
    }

    @Test
    fun `current filtering label is transactions`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val sortTypeFlow = flowOf(FuelConsumptionSortType.BY_DATE)
        val results = useCase(searchQueryFlow, sortTypeFlow).first()
        advanceUntilIdle()

        assertTrue(results is Async.Success)
        assertEquals(R.string.transactions, results.data.filteringUiInfo.currentFilteringLabel)
    }

    private fun emitSuccessData() {
        fakeObserveGroupedFuelConsumptionMonthlyUseCase.emit(Async.Success(fakeGroupedFuelConsumption))
        fakeObserveFuelConsumptionSortingUseCase.emit(Async.Success(fakeSorting))
    }

    companion object {

        private val fakeFuelConsumption = listOf(
            FuelConsumption("fc_1", BigDecimal("45.5"), BigDecimal("2.35"), "EUR", "Mercedes w140", 150000, 1000L, false, 1000L),
            FuelConsumption("fc_2", BigDecimal("40.0"), BigDecimal("2.40"), "EUR", "Mercedes w140", 149500, 2000L, false, 2000L),
        )

        private val fakeGroupedFuelConsumption = listOf(
            GroupedItems(
                group = "January 1970",
                total = listOf(FuelConsumptionTotal(BigDecimal("202.925"), "EUR")),
                items = fakeFuelConsumption,
            )
        )

        private val fakeSorting = listOf(
            SortMenuItem(type = FuelConsumptionSortType.BY_DATE, labelResId = R.string.sort_by_date),
            SortMenuItem(type = FuelConsumptionSortType.BY_MIN, labelResId = R.string.sort_by_min),
            SortMenuItem(type = FuelConsumptionSortType.BY_MAX, labelResId = R.string.sort_by_max),
        )

    }

}

