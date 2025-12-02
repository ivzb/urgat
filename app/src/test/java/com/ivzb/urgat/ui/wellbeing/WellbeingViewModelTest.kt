package com.ivzb.urgat.ui.wellbeing

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import com.ivzb.urgat.R
import com.ivzb.urgat.data.weight.Weight
import com.ivzb.urgat.domain.weight.FakeObserveWeightsUseCase
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

    private lateinit var fakeObserveWeightsUseCase: FakeObserveWeightsUseCase

    private lateinit var viewModel: WellbeingViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeObserveWeightsUseCase = FakeObserveWeightsUseCase()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveWeightsUseCase.emit(Async.Loading)
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        val initial = viewModel.uiState.value
        assertTrue(initial.isLoading)
        assertFalse(initial.hasError)
        assertTrue(initial.weights.isEmpty())
        assertNull(initial.uiEvent)

        job.cancel()
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        val fakeWeights = listOf(Weight("1", 80.toBigDecimal(), 1764523811, false, 1L))
        fakeObserveWeightsUseCase.emit(Async.Success(fakeWeights))
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(fakeWeights, state.weights)
        assertFalse(state.isLoading)
        assertFalse(state.hasError)

        job.cancel()
    }

    @Test
    fun `emits success state when use case returns empty data`() = runTest {
        fakeObserveWeightsUseCase.emit(Async.Success(listOf()))
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.weights.isEmpty())
        assertFalse(state.isLoading)
        assertFalse(state.hasError)

        job.cancel()
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveWeightsUseCase.emit(Async.Error(R.string.error_weights))
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
        val fakeWeights = listOf(Weight("2", 80.toBigDecimal(), 1764523811, false, 1L))
        fakeObserveWeightsUseCase.emit(Async.Success(fakeWeights))
        viewModel = createViewModel()

        viewModel.onIntent(WellbeingIntent.SearchQueryChanged("80"))

        advanceUntilIdle()

        assertEquals("80", fakeObserveWeightsUseCase.lastQuery)
    }

    @Test
    fun `header item click emits TrackWeight event`() = runTest {
        fakeObserveWeightsUseCase.emit(Async.Success(emptyList()))
        viewModel = createViewModel()

        viewModel.onIntent(
            WellbeingIntent.HeaderItemClick(
                WellbeingHeaderItem(WellbeingHeaderItemId.Weight, Icons.Filled.FitnessCenter, R.string.track_weight)
            )
        )

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val event = viewModel.uiState.value.uiEvent
        assertEquals(WellbeingUiEvent.TrackWeight, event)

        job.cancel()
    }

    @Test
    fun `onEventConsumed clears uiEvent`() = runTest {
        fakeObserveWeightsUseCase.emit(Async.Success(emptyList()))
        viewModel = createViewModel()

        viewModel.onIntent(
            WellbeingIntent.HeaderItemClick(
                WellbeingHeaderItem(WellbeingHeaderItemId.Weight, Icons.Filled.FitnessCenter, R.string.track_weight)
            )
        )

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()
        assertEquals(WellbeingUiEvent.TrackWeight, viewModel.uiState.value.uiEvent)

        viewModel.onIntent(WellbeingIntent.EventConsumed)
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.uiEvent)

        job.cancel()
    }

    private fun createViewModel() = WellbeingViewModel(
        fakeObserveWeightsUseCase
    )
}
