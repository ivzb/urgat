package com.ivzb.urgat.ui.savings

import androidx.lifecycle.SavedStateHandle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.saving.SavingCategory
import com.ivzb.urgat.domain.saving.FakeObserveGroupedSavingCategoriesAllTimeUseCase
import com.ivzb.urgat.domain.saving.FakeObserveGroupedSavingCategoriesMonthlyUseCase
import com.ivzb.urgat.domain.saving.FakeObserveGroupedSavingCategoriesYearlyUseCase
import com.ivzb.urgat.domain.saving.FakeObserveGroupedSavingsMonthlyUseCase
import com.ivzb.urgat.domain.saving.FakeObserveSavingsSortingUseCase
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
class SavingsViewModelTest {

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var fakeObserveSavingsUseCase: FakeObserveGroupedSavingsMonthlyUseCase
    private lateinit var fakeObserveGroupedSavingCategoriesMonthlyUseCase: FakeObserveGroupedSavingCategoriesMonthlyUseCase
    private lateinit var fakeObserveGroupedSavingCategoriesYearlyUseCase: FakeObserveGroupedSavingCategoriesYearlyUseCase
    private lateinit var fakeObserveGroupedSavingCategoriesAllTimeUseCase: FakeObserveGroupedSavingCategoriesAllTimeUseCase
    private lateinit var fakeObserveSavingsSortingUseCase: FakeObserveSavingsSortingUseCase

    private lateinit var viewModel: SavingsViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle(mapOf())
        fakeObserveSavingsUseCase = FakeObserveGroupedSavingsMonthlyUseCase()
        fakeObserveGroupedSavingCategoriesMonthlyUseCase = FakeObserveGroupedSavingCategoriesMonthlyUseCase()
        fakeObserveGroupedSavingCategoriesYearlyUseCase = FakeObserveGroupedSavingCategoriesYearlyUseCase()
        fakeObserveGroupedSavingCategoriesAllTimeUseCase = FakeObserveGroupedSavingCategoriesAllTimeUseCase()
        fakeObserveSavingsSortingUseCase = FakeObserveSavingsSortingUseCase()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveSavingsUseCase.emit(Async.Loading)
        fakeObserveGroupedSavingCategoriesMonthlyUseCase.emit(Async.Loading)
        fakeObserveGroupedSavingCategoriesYearlyUseCase.emit(Async.Loading)
        fakeObserveGroupedSavingCategoriesAllTimeUseCase.emit(Async.Loading)
        fakeObserveSavingsSortingUseCase.emit(Async.Loading)
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        val initial = viewModel.uiState.value
        assertTrue(initial.isLoading)
        assertFalse(initial.hasError)
        assertTrue(initial.savings.isEmpty())
        assertTrue(initial.categoriesMonthly.isEmpty())
        assertTrue(initial.categoriesYearly.isEmpty())
        assertTrue(initial.categoriesAllTime.isEmpty())

        job.cancel()
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        val savings = listOf(Saving("1", "Emergency fund", BigDecimal.TEN, 1L, false, 1L))
        val fakeSavings = listOf(
            GroupedItems(
                group = "2025/12",
                total = "10".toBigDecimal(),
                items = savings,
            )
        )
        val fakeCategoriesMonthly = listOf(
            GroupedItems(
                group = "2025/12",
                total = "10".toBigDecimal(),
                items = listOf(
                    SavingCategory(
                        id = "1",
                        name = "food",
                        value = "10".toBigDecimal(),
                        savings = savings,
                    )
                )
            )
        )
        val fakeCategoriesYearly = listOf(
            GroupedItems(
                group = "2025",
                total = "10".toBigDecimal(),
                items = listOf(
                    SavingCategory(
                        id = "1",
                        name = "food",
                        value = "10".toBigDecimal(),
                        savings = savings,
                    )
                )
            )
        )
        val fakeCategoriesAllTime = listOf(
            SavingCategory(
                id = "1",
                name = "food",
                value = "10".toBigDecimal(),
                savings = savings,
            )
        )
        val fakeSavingsSorting = listOf(
            SortMenuItem(type = SavingsSortType.BY_DATE, labelResId = R.string.sort_by_date),
            SortMenuItem(type = SavingsSortType.BY_MIN, labelResId = R.string.sort_by_min),
            SortMenuItem(type = SavingsSortType.BY_MAX, labelResId = R.string.sort_by_max),
        )

        fakeObserveSavingsUseCase.emit(Async.Success(fakeSavings))
        fakeObserveGroupedSavingCategoriesMonthlyUseCase.emit(Async.Success(fakeCategoriesMonthly))
        fakeObserveGroupedSavingCategoriesYearlyUseCase.emit(Async.Success(fakeCategoriesYearly))
        fakeObserveGroupedSavingCategoriesAllTimeUseCase.emit(Async.Success(fakeCategoriesAllTime))
        fakeObserveSavingsSortingUseCase.emit(Async.Success(fakeSavingsSorting))
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(fakeSavings, state.savings)
        assertEquals(fakeCategoriesMonthly, state.categoriesMonthly)
        assertEquals(fakeCategoriesYearly, state.categoriesYearly)
        assertEquals(fakeCategoriesAllTime, state.categoriesAllTime)
        assertEquals(fakeSavingsSorting, state.filteringUiInfo.sortMenuItems)
        assertFalse(state.isLoading)
        assertFalse(state.hasError)

        job.cancel()
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveSavingsUseCase.emit(Async.Error)
        fakeObserveGroupedSavingCategoriesMonthlyUseCase.emit(Async.Error)
        fakeObserveGroupedSavingCategoriesYearlyUseCase.emit(Async.Error)
        fakeObserveGroupedSavingCategoriesAllTimeUseCase.emit(Async.Error)
        fakeObserveSavingsSortingUseCase.emit(Async.Error)
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
        val savings = listOf(Saving("1", "Emergency fund", BigDecimal.TEN, 1L, false, 1L))
        val fakeSavings = listOf(GroupedItems(
            group = "2025/12",
            total = "10".toBigDecimal(),
            items = savings,
        ))
        val fakeCategoriesMonthly = listOf(
            GroupedItems(
                group = "2025/12",
                total = "10".toBigDecimal(),
                items = listOf(
                    SavingCategory(
                        id = "1",
                        name = "food",
                        value = "10".toBigDecimal(),
                        savings = savings,
                    )
                )
            )
        )
        val fakeCategoriesYearly = listOf(
            GroupedItems(
                group = "2025",
                total = "10".toBigDecimal(),
                items = listOf(
                    SavingCategory(
                        id = "1",
                        name = "food",
                        value = "10".toBigDecimal(),
                        savings = savings,
                    )
                )
            )
        )
        val fakeCategoriesAllTime = listOf(
            SavingCategory(
                id = "1",
                name = "food",
                value = "10".toBigDecimal(),
                savings = savings,
            )
        )

        fakeObserveSavingsUseCase.emit(Async.Success(fakeSavings))
        fakeObserveGroupedSavingCategoriesMonthlyUseCase.emit(Async.Success(fakeCategoriesMonthly))
        fakeObserveGroupedSavingCategoriesYearlyUseCase.emit(Async.Success(fakeCategoriesYearly))
        fakeObserveGroupedSavingCategoriesAllTimeUseCase.emit(Async.Success(fakeCategoriesAllTime))
        fakeObserveSavingsSortingUseCase.emit(Async.Success(listOf()))
        viewModel = createViewModel()

        viewModel.onIntent(SavingsIntent.SearchQueryChanged("coffee"))

        advanceUntilIdle()

        assertEquals("coffee", fakeObserveSavingsUseCase.lastQuery)
    }

    @Test
    fun `setFiltering updates saved state handle`() = runTest {
        fakeObserveSavingsUseCase.emit(Async.Success(emptyList()))
        fakeObserveGroupedSavingCategoriesMonthlyUseCase.emit(Async.Success(emptyList()))
        fakeObserveGroupedSavingCategoriesYearlyUseCase.emit(Async.Success(emptyList()))
        fakeObserveGroupedSavingCategoriesAllTimeUseCase.emit(Async.Success(emptyList()))
        fakeObserveSavingsSortingUseCase.emit(Async.Success(listOf()))
        viewModel = createViewModel()

        val expected = SavingsSortType.BY_MIN

        viewModel.onIntent(SavingsIntent.SetSorting(expected))
        advanceUntilIdle()
    }

    private fun createViewModel() = SavingsViewModel(
        fakeObserveSavingsUseCase,
        fakeObserveGroupedSavingCategoriesMonthlyUseCase,
        fakeObserveGroupedSavingCategoriesYearlyUseCase,
        fakeObserveGroupedSavingCategoriesAllTimeUseCase,
        fakeObserveSavingsSortingUseCase,
        savedStateHandle
    )
}