package com.ivzb.urgat.ui.weights

import androidx.lifecycle.SavedStateHandle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.weight.Weight
import com.ivzb.urgat.domain.weight.FakeObserveWeightsSortingUseCase
import com.ivzb.urgat.domain.weight.FakeObserveWeightsUseCase
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import kotlinx.coroutines.flow.collect
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class WeightsViewModelTest {

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var fakeObserveWeightsUseCase: FakeObserveWeightsUseCase
    private lateinit var fakeObserveWeightsSortingUseCase: FakeObserveWeightsSortingUseCase

    private lateinit var viewModel: WeightsViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        savedStateHandle = SavedStateHandle(mapOf())
        fakeObserveWeightsUseCase = FakeObserveWeightsUseCase()
        fakeObserveWeightsSortingUseCase = FakeObserveWeightsSortingUseCase()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveWeightsUseCase.emit(Async.Loading)
        fakeObserveWeightsSortingUseCase.emit(Async.Loading)
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        val initial = viewModel.uiState.value
        assertTrue(initial.isLoading)
        assertFalse(initial.hasError)
        assertTrue(initial.weights.isEmpty())

        job.cancel()
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        emitSuccessData()
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(fakeWeights, state.weights)
        assertEquals(fakeWeightsSorting, state.filteringUiInfo.sortMenuItems)
        assertFalse(state.filteringUiInfo.showSortBadge)
        assertFalse(state.isLoading)
        assertFalse(state.hasError)

        job.cancel()
    }

    @Test
    fun `emits success state when use case returns data with sort badge`() = runTest {
        emitSuccessData()
        savedStateHandle = SavedStateHandle(mapOf(WEIGHTS_SORT_SAVED_STATE_KEY to WeightsSortType.BY_MIN))
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(fakeWeights, state.weights)
        assertEquals(fakeWeightsSorting, state.filteringUiInfo.sortMenuItems)
        assertTrue(state.filteringUiInfo.showSortBadge)
        assertFalse(state.isLoading)
        assertFalse(state.hasError)

        job.cancel()
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveWeightsUseCase.emit(Async.Error)
        fakeObserveWeightsSortingUseCase.emit(Async.Error)
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
    fun `setSorting updates saved state handle`() = runTest {
        fakeObserveWeightsUseCase.emit(Async.Success(listOf()))
        fakeObserveWeightsSortingUseCase.emit(Async.Success(listOf()))
        viewModel = createViewModel()

        val expected = WeightsSortType.BY_MIN

        viewModel.onIntent(WeightsIntent.SetSorting(expected))
        advanceUntilIdle()

        assertEquals(
            expected,
            savedStateHandle[WEIGHTS_SORT_SAVED_STATE_KEY]
        )
    }

    private fun createViewModel() = WeightsViewModel(
        fakeObserveWeightsUseCase,
        fakeObserveWeightsSortingUseCase,
        savedStateHandle,
    )

    private fun emitSuccessData() {
        fakeObserveWeightsUseCase.emit(Async.Success(fakeWeights))
        fakeObserveWeightsSortingUseCase.emit(Async.Success(fakeWeightsSorting))
    }

    companion object {

        private val fakeWeights = listOf(Weight("1", "80.5".toBigDecimal(), 1L, false, 1L))

        private val fakeWeightsSorting = listOf(
            SortMenuItem(type = WeightsSortType.BY_DATE, labelResId = R.string.sort_by_date),
            SortMenuItem(type = WeightsSortType.BY_MIN, labelResId = R.string.sort_by_min),
            SortMenuItem(type = WeightsSortType.BY_MAX, labelResId = R.string.sort_by_max),
        )

    }
}
