package com.ivzb.urgat.domain.saving

import com.ivzb.urgat.R
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.saving.SavingCategory
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.ui.savings.SavingsSortType
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
class ObserveSavingsUiStateUseCaseTest {

    private lateinit var fakeObserveSavingsUseCase: FakeObserveGroupedSavingsMonthlyUseCase
    private lateinit var fakeObserveGroupedSavingCategoriesMonthlyUseCase: FakeObserveGroupedSavingCategoriesMonthlyUseCase
    private lateinit var fakeObserveGroupedSavingCategoriesYearlyUseCase: FakeObserveGroupedSavingCategoriesYearlyUseCase
    private lateinit var fakeObserveGroupedSavingCategoriesAllTimeUseCase: FakeObserveGroupedSavingCategoriesAllTimeUseCase
    private lateinit var fakeObserveSavingsSortingUseCase: FakeObserveSavingsSortingUseCase

    private lateinit var useCase: ObserveSavingsUiStateUseCase

    @Before
    fun setUp() {
        fakeObserveSavingsUseCase = FakeObserveGroupedSavingsMonthlyUseCase()
        fakeObserveGroupedSavingCategoriesMonthlyUseCase = FakeObserveGroupedSavingCategoriesMonthlyUseCase()
        fakeObserveGroupedSavingCategoriesYearlyUseCase = FakeObserveGroupedSavingCategoriesYearlyUseCase()
        fakeObserveGroupedSavingCategoriesAllTimeUseCase = FakeObserveGroupedSavingCategoriesAllTimeUseCase()
        fakeObserveSavingsSortingUseCase = FakeObserveSavingsSortingUseCase()

        useCase = DefaultObserveSavingsUiStateUseCase(
            fakeObserveSavingsUseCase,
            fakeObserveGroupedSavingCategoriesMonthlyUseCase,
            fakeObserveGroupedSavingCategoriesYearlyUseCase,
            fakeObserveGroupedSavingCategoriesAllTimeUseCase,
            fakeObserveSavingsSortingUseCase,
        )
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveSavingsUseCase.emit(Async.Loading)
        fakeObserveGroupedSavingCategoriesMonthlyUseCase.emit(Async.Loading)
        fakeObserveGroupedSavingCategoriesYearlyUseCase.emit(Async.Loading)
        fakeObserveGroupedSavingCategoriesAllTimeUseCase.emit(Async.Loading)
        fakeObserveSavingsSortingUseCase.emit(Async.Loading)

        val searchQueryFlow = flowOf("")
        val sortTypeFlow = flowOf(SavingsSortType.BY_DATE)

        val results = useCase(searchQueryFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.isLoading)
        assertFalse(uiState.hasError)
        assertTrue(uiState.savings.isEmpty())
        assertTrue(uiState.categoriesMonthly.isEmpty())
        assertTrue(uiState.categoriesYearly.isEmpty())
        assertTrue(uiState.categoriesAllTime.isEmpty())
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val sortTypeFlow = flowOf(SavingsSortType.BY_DATE)

        val results = useCase(searchQueryFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertEquals(fakeSavings, uiState.savings)
        assertEquals(fakeCategoriesMonthly, uiState.categoriesMonthly)
        assertEquals(fakeCategoriesYearly, uiState.categoriesYearly)
        assertEquals(fakeCategoriesAllTime, uiState.categoriesAllTime)
        assertEquals(fakeSavingsSorting, uiState.filteringUiInfo.sortMenuItems)
        assertFalse(uiState.filteringUiInfo.showSortBadge)
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `emits success state when use case returns data with sort badge`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val sortTypeFlow = flowOf(SavingsSortType.BY_MAX)

        val results = useCase(searchQueryFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertEquals(fakeSavings, uiState.savings)
        assertEquals(fakeCategoriesMonthly, uiState.categoriesMonthly)
        assertEquals(fakeCategoriesYearly, uiState.categoriesYearly)
        assertEquals(fakeCategoriesAllTime, uiState.categoriesAllTime)
        assertEquals(fakeSavingsSorting, uiState.filteringUiInfo.sortMenuItems)
        assertTrue(uiState.filteringUiInfo.showSortBadge)
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveSavingsUseCase.emit(Async.Error)
        fakeObserveGroupedSavingCategoriesMonthlyUseCase.emit(Async.Error)
        fakeObserveGroupedSavingCategoriesYearlyUseCase.emit(Async.Error)
        fakeObserveGroupedSavingCategoriesAllTimeUseCase.emit(Async.Error)
        fakeObserveSavingsSortingUseCase.emit(Async.Error)

        val searchQueryFlow = flowOf("")
        val sortTypeFlow = flowOf(SavingsSortType.BY_DATE)

        val results = useCase(searchQueryFlow, sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.hasError)
        assertFalse(uiState.isLoading)
    }

    private fun emitSuccessData() {
        fakeObserveSavingsUseCase.emit(Async.Success(fakeSavings))
        fakeObserveGroupedSavingCategoriesMonthlyUseCase.emit(Async.Success(fakeCategoriesMonthly))
        fakeObserveGroupedSavingCategoriesYearlyUseCase.emit(Async.Success(fakeCategoriesYearly))
        fakeObserveGroupedSavingCategoriesAllTimeUseCase.emit(Async.Success(fakeCategoriesAllTime))
        fakeObserveSavingsSortingUseCase.emit(Async.Success(fakeSavingsSorting))
    }

    companion object {

        private val savings = listOf(Saving("1", "Emergency fund", BigDecimal.TEN, 1L, false, 1L))

        private val fakeSavings = listOf(
            GroupedItems(group = "2025/12", total = "10".toBigDecimal(), items = savings)
        )

        private val fakeCategoriesMonthly = listOf(
            GroupedItems(
                group = "2025/12", total = "10".toBigDecimal(),
                items = listOf(SavingCategory(id = "1", name = "food", value = "10".toBigDecimal(), savings = savings))
            )
        )

        private val fakeCategoriesYearly = listOf(
            GroupedItems(
                group = "2025", total = "10".toBigDecimal(),
                items = listOf(SavingCategory(id = "1", name = "food", value = "10".toBigDecimal(), savings = savings))
            )
        )

        private val fakeCategoriesAllTime = listOf(
            SavingCategory(id = "1", name = "food", value = "10".toBigDecimal(), savings = savings)
        )

        private val fakeSavingsSorting = listOf(
            SortMenuItem(type = SavingsSortType.BY_DATE, labelResId = R.string.sort_by_date),
            SortMenuItem(type = SavingsSortType.BY_MIN, labelResId = R.string.sort_by_min),
            SortMenuItem(type = SavingsSortType.BY_MAX, labelResId = R.string.sort_by_max),
        )

    }

}
