package com.ivzb.urgat.ui.fuelConsumption

import com.ivzb.urgat.R
import com.ivzb.urgat.data.car.FakeCarRepository
import com.ivzb.urgat.data.fuelConsumption.FuelConsumption
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionCategory
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionTotal
import com.ivzb.urgat.domain.fuelConsumption.FakeObserveFuelConsumptionUiStateUseCase
import com.ivzb.urgat.domain.fuelConsumption.GroupedFuelConsumption
import com.ivzb.urgat.domain.fuelConsumption.GroupedFuelConsumptionCategories
import com.ivzb.urgat.ui.components.FilterMenuItem
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
class FuelConsumptionViewModelTest {

    private lateinit var fakeCarRepository: FakeCarRepository
    private lateinit var fakeObserveFuelConsumptionUiStateUseCase: FakeObserveFuelConsumptionUiStateUseCase

    private lateinit var viewModel: FuelConsumptionViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeCarRepository = FakeCarRepository()
        fakeObserveFuelConsumptionUiStateUseCase = FakeObserveFuelConsumptionUiStateUseCase()
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
        assertTrue(initial.fuelConsumption.isEmpty())
        assertTrue(initial.categoriesMonthly.isEmpty())
        assertTrue(initial.categoriesYearly.isEmpty())
        assertTrue(initial.categoriesAllTime.isEmpty())

        job.cancel()
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        fakeObserveFuelConsumptionUiStateUseCase.emit(Async.Success(fakeUiState))
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(fakeGroupedFuelConsumption, state.fuelConsumption)
        assertEquals(fakeCategoriesMonthly, state.categoriesMonthly)
        assertEquals(fakeCategoriesYearly, state.categoriesYearly)
        assertEquals(fakeCategoriesAllTime, state.categoriesAllTime)
        assertEquals(fakeFiltering, state.filteringUiInfo.filterMenuItems)
        assertEquals(fakeSorting, state.filteringUiInfo.sortMenuItems)
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
    fun `search query intent updates repository and use case input`() = runTest {
        fakeObserveFuelConsumptionUiStateUseCase.emit(Async.Success(FuelConsumptionUiState()))
        viewModel = createViewModel()

        viewModel.onIntent(FuelConsumptionIntent.SearchQueryChanged("diesel"))
        advanceUntilIdle()

        assertEquals("diesel", fakeCarRepository.searchQueryFlow.value)
        assertEquals("diesel", fakeObserveFuelConsumptionUiStateUseCase.lastQuery)
    }

    @Test
    fun `set filtering updates repository and use case input`() = runTest {
        fakeObserveFuelConsumptionUiStateUseCase.emit(Async.Success(FuelConsumptionUiState()))
        viewModel = createViewModel()

        viewModel.onIntent(FuelConsumptionIntent.SetFiltering("Diesel"))
        advanceUntilIdle()

        assertEquals("Diesel", fakeCarRepository.fuelConsumptionFilterTypeFlow.value)
        assertEquals("Diesel", fakeObserveFuelConsumptionUiStateUseCase.lastFilterType)
    }

    @Test
    fun `set sorting updates repository`() = runTest {
        viewModel = createViewModel()

        viewModel.onIntent(FuelConsumptionIntent.SetSorting(FuelConsumptionSortType.BY_MAX))
        advanceUntilIdle()

        assertEquals(FuelConsumptionSortType.BY_MAX, fakeCarRepository.fuelConsumptionSortTypeFlow.value)
    }

    @Test
    fun `reset filters resets search query filter type and sort type to defaults`() = runTest {
        viewModel = createViewModel()

        viewModel.onIntent(FuelConsumptionIntent.SearchQueryChanged("diesel"))
        viewModel.onIntent(FuelConsumptionIntent.SetFiltering("Diesel"))
        viewModel.onIntent(FuelConsumptionIntent.SetSorting(FuelConsumptionSortType.BY_MAX))
        advanceUntilIdle()

        assertEquals("diesel", fakeCarRepository.searchQueryFlow.value)
        assertEquals("Diesel", fakeCarRepository.fuelConsumptionFilterTypeFlow.value)
        assertEquals(FuelConsumptionSortType.BY_MAX, fakeCarRepository.fuelConsumptionSortTypeFlow.value)

        viewModel.onIntent(FuelConsumptionIntent.ResetFilters)
        advanceUntilIdle()

        assertEquals("", fakeCarRepository.searchQueryFlow.value)
        assertEquals(FuelConsumptionFilterType_All, fakeCarRepository.fuelConsumptionFilterTypeFlow.value)
        assertEquals(FuelConsumptionSortType.BY_DATE, fakeCarRepository.fuelConsumptionSortTypeFlow.value)
    }

    private fun createViewModel() = FuelConsumptionViewModel(
        fakeObserveFuelConsumptionUiStateUseCase,
        fakeCarRepository,
    )

    companion object {

        private val fakeFuelConsumption = listOf(
            FuelConsumption("fc_1", BigDecimal("45.5"), BigDecimal("2.35"), "EUR", "Diesel", "Mercedes w140", 150000, 500, 1000L, false, 1000L),
            FuelConsumption("fc_2", BigDecimal("40.0"), BigDecimal("2.40"), "EUR", "Diesel", "Mercedes w140", 149500, 600, 2000L, false, 2000L),
        )

        private val fakeGroupedFuelConsumption: List<GroupedFuelConsumption> = listOf(
            GroupedFuelConsumption(
                group = "January 1970",
                total = listOf(FuelConsumptionTotal("Diesel", BigDecimal("85.5"))),
                items = fakeFuelConsumption,
            )
        )

        private val fakeCategoriesMonthly: List<GroupedFuelConsumptionCategories> = listOf(
            GroupedFuelConsumptionCategories(
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

        private val fakeCategoriesYearly: List<GroupedFuelConsumptionCategories> = listOf(
            GroupedFuelConsumptionCategories(
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

        private val fakeUiState = FuelConsumptionUiState(
            fuelConsumption = fakeGroupedFuelConsumption,
            categoriesMonthly = fakeCategoriesMonthly,
            categoriesYearly = fakeCategoriesYearly,
            categoriesAllTime = fakeCategoriesAllTime,
            filteringUiInfo = FuelConsumptionFilteringUiInfo(
                filterMenuItems = fakeFiltering,
                sortMenuItems = fakeSorting,
            ),
        )
    }
}

