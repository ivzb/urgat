package com.ivzb.urgat.domain.weight

import com.ivzb.urgat.R
import com.ivzb.urgat.data.weight.Weight
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.ui.weights.WeightsSortType
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
class ObserveWeightsUiStateUseCaseTest {

    private lateinit var fakeObserveWeightsUseCase: FakeObserveWeightsUseCase
    private lateinit var fakeObserveWeightsSortingUseCase: FakeObserveWeightsSortingUseCase

    private lateinit var useCase: ObserveWeightsUiStateUseCase

    @Before
    fun setUp() {
        fakeObserveWeightsUseCase = FakeObserveWeightsUseCase()
        fakeObserveWeightsSortingUseCase = FakeObserveWeightsSortingUseCase()

        useCase = DefaultObserveWeightsUiStateUseCase(
            fakeObserveWeightsUseCase,
            fakeObserveWeightsSortingUseCase,
        )
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveWeightsUseCase.emit(Async.Loading)
        fakeObserveWeightsSortingUseCase.emit(Async.Loading)

        val sortTypeFlow = flowOf(WeightsSortType.BY_DATE)

        val results = useCase(sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.isLoading)
        assertFalse(uiState.hasError)
        assertTrue(uiState.weights.isEmpty())
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        emitSuccessData()

        val sortTypeFlow = flowOf(WeightsSortType.BY_DATE)

        val results = useCase(sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertEquals(fakeWeights, uiState.weights)
        assertEquals(fakeWeightsSorting, uiState.filteringUiInfo.sortMenuItems)
        assertFalse(uiState.filteringUiInfo.showSortBadge)
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `emits success state when use case returns data with sort badge`() = runTest {
        emitSuccessData()

        val sortTypeFlow = flowOf(WeightsSortType.BY_MAX)

        val results = useCase(sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertEquals(fakeWeights, uiState.weights)
        assertEquals(fakeWeightsSorting, uiState.filteringUiInfo.sortMenuItems)
        assertTrue(uiState.filteringUiInfo.showSortBadge)
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `emits success state when use case returns empty data`() = runTest {
        fakeObserveWeightsUseCase.emit(Async.Success(listOf()))
        fakeObserveWeightsSortingUseCase.emit(Async.Success(fakeWeightsSorting))

        val sortTypeFlow = flowOf(WeightsSortType.BY_DATE)

        val results = useCase(sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.weights.isEmpty())
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveWeightsUseCase.emit(Async.Error)
        fakeObserveWeightsSortingUseCase.emit(Async.Success(fakeWeightsSorting))

        val sortTypeFlow = flowOf(WeightsSortType.BY_DATE)

        val results = useCase(sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.hasError)
        assertFalse(uiState.isLoading)
    }

    @Test
    fun `showSortBadge is false when sort type is BY_DATE`() = runTest {
        emitSuccessData()

        val sortTypeFlow = flowOf(WeightsSortType.BY_DATE)

        val results = useCase(sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertFalse(uiState.filteringUiInfo.showSortBadge)
    }

    @Test
    fun `showSortBadge is true when sort type is BY_MIN`() = runTest {
        emitSuccessData()

        val sortTypeFlow = flowOf(WeightsSortType.BY_MIN)

        val results = useCase(sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.filteringUiInfo.showSortBadge)
    }

    @Test
    fun `showSortBadge is true when sort type is BY_MAX`() = runTest {
        emitSuccessData()

        val sortTypeFlow = flowOf(WeightsSortType.BY_MAX)

        val results = useCase(sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.filteringUiInfo.showSortBadge)
    }

    @Test
    fun `currentFilteringLabel is all_weights`() = runTest {
        emitSuccessData()

        val sortTypeFlow = flowOf(WeightsSortType.BY_DATE)

        val results = useCase(sortTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertEquals(R.string.all_weights, uiState.filteringUiInfo.currentFilteringLabel)
    }

    private fun emitSuccessData() {
        fakeObserveWeightsUseCase.emit(Async.Success(fakeWeights))
        fakeObserveWeightsSortingUseCase.emit(Async.Success(fakeWeightsSorting))
    }

    companion object {

        private val fakeWeights = listOf(
            Weight("1", 80.toBigDecimal(), 1764523811, false, 1L),
            Weight("2", 81.5.toBigDecimal(), 1764610211, false, 2L),
        )

        private val fakeWeightsSorting = listOf(
            SortMenuItem(type = WeightsSortType.BY_DATE, labelResId = R.string.sort_by_date),
            SortMenuItem(type = WeightsSortType.BY_MIN, labelResId = R.string.sort_by_min),
            SortMenuItem(type = WeightsSortType.BY_MAX, labelResId = R.string.sort_by_max),
        )

    }

}

