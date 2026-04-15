package com.ivzb.urgat.ui.car

import com.ivzb.urgat.R
import com.ivzb.urgat.data.car.FakeCarRepository
import com.ivzb.urgat.data.fuelConsumption.FuelConsumption
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionTotal
import com.ivzb.urgat.domain.fuelConsumption.FakeObserveFuelConsumptionUiStateUseCase
import com.ivzb.urgat.domain.fuelConsumption.GroupedFuelConsumption
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionFilterType_All
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionFilteringUiInfo
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionSortType
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionUiState
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CarViewModelTest {

    private lateinit var fakeObserveFuelConsumptionUiStateUseCase: FakeObserveFuelConsumptionUiStateUseCase
    private lateinit var fakeCarRepository: FakeCarRepository

    private lateinit var viewModel: CarViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeObserveFuelConsumptionUiStateUseCase = FakeObserveFuelConsumptionUiStateUseCase()
        fakeCarRepository = FakeCarRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveFuelConsumptionUiStateUseCase.emit(Async.Loading)
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        val initial = viewModel.uiState.value
        assertTrue(initial.isLoading)
        assertFalse(initial.hasError)
        assertTrue(initial.fuelConsumptionUiState.fuelConsumption.isEmpty())
        assertNull(initial.uiEvent)

        job.cancel()
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        emitSuccessData(showSortBadge = false)
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(fakeGroupedFuelConsumption, state.fuelConsumptionUiState.fuelConsumption)
        assertEquals(fakeSorting, state.fuelConsumptionUiState.filteringUiInfo.sortMenuItems)
        assertFalse(state.fuelConsumptionUiState.filteringUiInfo.showSortBadge)
        assertFalse(state.isLoading)
        assertFalse(state.hasError)

        job.cancel()
    }

    @Test
    fun `emits success state when use case returns data with sort badge`() = runTest {
        emitSuccessData(showSortBadge = true)
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(fakeGroupedFuelConsumption, state.fuelConsumptionUiState.fuelConsumption)
        assertEquals(fakeSorting, state.fuelConsumptionUiState.filteringUiInfo.sortMenuItems)
        assertTrue(state.fuelConsumptionUiState.filteringUiInfo.showSortBadge)
        assertFalse(state.isLoading)
        assertFalse(state.hasError)

        job.cancel()
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveFuelConsumptionUiStateUseCase.emit(Async.Error)
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
    fun `emits empty state when use case returns empty data`() = runTest {
        fakeObserveFuelConsumptionUiStateUseCase.emit(
            Async.Success(FuelConsumptionUiState(
                fuelConsumption = listOf(),
            ))
        )
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.fuelConsumptionUiState.fuelConsumption.isEmpty())
        assertFalse(state.isLoading)
        assertFalse(state.hasError)

        job.cancel()
    }

    @Test
    fun `onSearchQueryChanged updates query and triggers new emission`() = runTest {
        fakeObserveFuelConsumptionUiStateUseCase.emit(Async.Success(FuelConsumptionUiState()))
        viewModel = createViewModel()

        viewModel.onIntent(CarIntent.SearchQueryChanged("coffee"))

        advanceUntilIdle()

        assertEquals("coffee", fakeObserveFuelConsumptionUiStateUseCase.lastQuery)
    }

    @Test
    fun `fab action click emits AddFuelConsumption event`() = runTest {
        fakeObserveFuelConsumptionUiStateUseCase.emit(Async.Success(FuelConsumptionUiState()))
        viewModel = createViewModel()

        viewModel.onIntent(CarIntent.FabActionClick(CarPage.FuelConsumption))

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val event = viewModel.uiState.value.uiEvent
        assertEquals(CarUiEvent.AddFuelConsumption, event)

        job.cancel()
    }

    @Test
    fun `event consumed clears uiEvent`() = runTest {
        fakeObserveFuelConsumptionUiStateUseCase.emit(Async.Success(FuelConsumptionUiState()))
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        viewModel.onIntent(CarIntent.FabActionClick(CarPage.FuelConsumption))
        advanceUntilIdle()
        assertEquals(CarUiEvent.AddFuelConsumption, viewModel.uiState.value.uiEvent)

        viewModel.onIntent(CarIntent.EventConsumed)
        advanceUntilIdle()
        assertNull(viewModel.uiState.value.uiEvent)

        job.cancel()
    }

    @Test
    fun `SetFuelConsumptionSorting updates repository`() = runTest {
        viewModel = createViewModel()

        val expected = FuelConsumptionSortType.BY_MAX

        viewModel.onIntent(CarIntent.SetFuelConsumptionSorting(expected))
        advanceUntilIdle()

        assertEquals(expected, fakeCarRepository.fuelConsumptionSortTypeFlow.value)
    }

    @Test
    fun `SetFuelConsumptionFiltering updates repository`() = runTest {
        viewModel = createViewModel()

        val expected = "Diesel"

        viewModel.onIntent(CarIntent.SetFuelConsumptionFiltering(expected))
        advanceUntilIdle()

        assertEquals(expected, fakeCarRepository.fuelConsumptionFilterTypeFlow.value)
    }

    private fun createViewModel() = CarViewModel(
        fakeObserveFuelConsumptionUiStateUseCase,
        fakeCarRepository,
    )

    private fun emitSuccessData(showSortBadge: Boolean) {
        fakeObserveFuelConsumptionUiStateUseCase.emit(Async.Success(FuelConsumptionUiState(
            fuelConsumption = fakeGroupedFuelConsumption,
            filteringUiInfo = FuelConsumptionFilteringUiInfo(
                sortMenuItems = fakeSorting,
                showSortBadge = showSortBadge,
            ),
        )))
    }

    @Test
    fun `resetFilters resets search query to empty`() = runTest {
        viewModel = createViewModel()

        viewModel.onIntent(CarIntent.SearchQueryChanged("coffee"))
        advanceUntilIdle()
        assertEquals("coffee", fakeCarRepository.searchQueryFlow.value)

        viewModel.onIntent(CarIntent.ResetFilters)
        advanceUntilIdle()

        assertEquals("", fakeCarRepository.searchQueryFlow.value)
    }

    @Test
    fun `resetFilters resets fuel consumption sort type to default`() = runTest {
        viewModel = createViewModel()

        viewModel.onIntent(CarIntent.SetFuelConsumptionSorting(FuelConsumptionSortType.BY_MAX))
        advanceUntilIdle()
        assertEquals(FuelConsumptionSortType.BY_MAX, fakeCarRepository.fuelConsumptionSortTypeFlow.value)

        viewModel.onIntent(CarIntent.ResetFilters)
        advanceUntilIdle()

        assertEquals(FuelConsumptionSortType.BY_DATE, fakeCarRepository.fuelConsumptionSortTypeFlow.value)
    }

    @Test
    fun `resetFilters resets search query filter type and sort type at once`() = runTest {
        viewModel = createViewModel()

        viewModel.onIntent(CarIntent.SearchQueryChanged("coffee"))
        viewModel.onIntent(CarIntent.SetFuelConsumptionFiltering("Diesel"))
        viewModel.onIntent(CarIntent.SetFuelConsumptionSorting(FuelConsumptionSortType.BY_MAX))
        advanceUntilIdle()

        assertEquals("coffee", fakeCarRepository.searchQueryFlow.value)
        assertEquals("Diesel", fakeCarRepository.fuelConsumptionFilterTypeFlow.value)
        assertEquals(FuelConsumptionSortType.BY_MAX, fakeCarRepository.fuelConsumptionSortTypeFlow.value)

        viewModel.onIntent(CarIntent.ResetFilters)
        advanceUntilIdle()

        assertEquals("", fakeCarRepository.searchQueryFlow.value)
        assertEquals(FuelConsumptionFilterType_All, fakeCarRepository.fuelConsumptionFilterTypeFlow.value)
        assertEquals(FuelConsumptionSortType.BY_DATE, fakeCarRepository.fuelConsumptionSortTypeFlow.value)
    }

    @Test
    fun `resetFilters clears search query in ui state`() = runTest {
        viewModel = createViewModel()

        viewModel.onIntent(CarIntent.SearchQueryChanged("coffee"))
        advanceUntilIdle()
        assertEquals("coffee", fakeCarRepository.searchQueryFlow.value)

        viewModel.onIntent(CarIntent.ResetFilters)
        advanceUntilIdle()

        assertEquals("", fakeCarRepository.searchQueryFlow.value)
    }

    companion object {
        
        private val fakeFuelConsumption = listOf(
            FuelConsumption("fc_1", BigDecimal("45.5"), BigDecimal("2.35"), "EUR", "Diesel", "Mercedes w140", 150000, 500, 1000L, false, 1000L),
            FuelConsumption("fc_2", BigDecimal("40.0"), BigDecimal("2.40"), "EUR", "Diesel", "Mercedes w140", 149500, 600, 2000L, false, 2000L),
        )

        private val fakeGroupedFuelConsumption = listOf(
            GroupedFuelConsumption(
                group = "January 1970",
                total = listOf(FuelConsumptionTotal("Diesel", BigDecimal("202.925"))),
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


