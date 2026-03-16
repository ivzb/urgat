package com.ivzb.urgat.ui.wellbeing

import androidx.lifecycle.SavedStateHandle
import com.ivzb.urgat.data.weight.Weight
import com.ivzb.urgat.domain.weight.FakeObserveWeightsUiStateUseCase
import com.ivzb.urgat.ui.weights.WeightsSortType
import com.ivzb.urgat.ui.weights.WeightsUiState
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class WellbeingViewModelTest {

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var fakeObserveWeightsUiStateUseCase: FakeObserveWeightsUiStateUseCase

    private lateinit var viewModel: WellbeingViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle(mapOf())
        fakeObserveWeightsUiStateUseCase = FakeObserveWeightsUiStateUseCase()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveWeightsUiStateUseCase.emit(Async.Loading)
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        val initial = viewModel.uiState.value
        assertTrue(initial.isLoading)
        assertFalse(initial.hasError)
        assertTrue(initial.weightsUiState.weights.isEmpty())
        assertNull(initial.uiEvent)

        job.cancel()
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        val fakeWeights = listOf(Weight("1", 80.toBigDecimal(), 1764523811, false, 1L))
        fakeObserveWeightsUiStateUseCase.emit(Async.Success(WeightsUiState(weights = fakeWeights)))
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(fakeWeights, state.weightsUiState.weights)
        assertFalse(state.isLoading)
        assertFalse(state.hasError)

        job.cancel()
    }

    @Test
    fun `emits success state when use case returns empty data`() = runTest {
        fakeObserveWeightsUiStateUseCase.emit(Async.Success(WeightsUiState(weights = listOf())))
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.weightsUiState.weights.isEmpty())
        assertFalse(state.isLoading)
        assertFalse(state.hasError)

        job.cancel()
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveWeightsUiStateUseCase.emit(Async.Error)
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
    fun `fab action click emits AddWeight event`() = runTest {
        fakeObserveWeightsUiStateUseCase.emit(Async.Success(WeightsUiState(weights = listOf())))
        viewModel = createViewModel()

        viewModel.onIntent(WellbeingIntent.FabActionClick(WellbeingFabActionId.Weight))

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val event = viewModel.uiState.value.uiEvent
        assertEquals(WellbeingUiEvent.AddWeight, event)

        job.cancel()
    }

    @Test
    fun `onEventConsumed clears uiEvent`() = runTest {
        fakeObserveWeightsUiStateUseCase.emit(Async.Success(WeightsUiState(weights = listOf())))
        viewModel = createViewModel()

        viewModel.onIntent(WellbeingIntent.FabActionClick(WellbeingFabActionId.Weight))

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()
        assertEquals(WellbeingUiEvent.AddWeight, viewModel.uiState.value.uiEvent)

        viewModel.onIntent(WellbeingIntent.EventConsumed)
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.uiEvent)

        job.cancel()
    }

    @Test
    fun `setWeightsSorting updates saved state handle`() = runTest {
        fakeObserveWeightsUiStateUseCase.emit(Async.Success(WeightsUiState(weights = listOf())))
        viewModel = createViewModel()

        val expected = WeightsSortType.BY_MIN

        viewModel.onIntent(WellbeingIntent.SetWeightsSorting(expected))
        advanceUntilIdle()

        assertEquals(
            expected,
            savedStateHandle[WELLBEING_WEIGHTS_SORT_SAVED_STATE_KEY]
        )
    }

    private fun createViewModel() = WellbeingViewModel(
        fakeObserveWeightsUiStateUseCase,
        savedStateHandle,
    )

}
