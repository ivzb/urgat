package com.ivzb.urgat.ui.wellbeing

import androidx.lifecycle.SavedStateHandle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.resources.ResourcesProvider
import com.ivzb.urgat.data.weight.Weight
import com.ivzb.urgat.domain.weight.FakeObserveWeightsSortingUseCase
import com.ivzb.urgat.domain.weight.FakeObserveWeightsUseCase
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.ui.weights.WeightsSortType
import com.ivzb.urgat.util.Async
import io.mockk.every
import io.mockk.mockk
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
    private lateinit var fakeObserveWeightsUseCase: FakeObserveWeightsUseCase
    private lateinit var fakeObserveWeightsSortingUseCase: FakeObserveWeightsSortingUseCase
    private lateinit var mockResourcesProvider: ResourcesProvider

    private lateinit var viewModel: WellbeingViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle(mapOf())
        fakeObserveWeightsUseCase = FakeObserveWeightsUseCase()
        fakeObserveWeightsSortingUseCase = FakeObserveWeightsSortingUseCase()
        mockResourcesProvider = mockk()

        every { mockResourcesProvider.getInteger(R.integer.items_limit) } returns 5
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
        assertNull(initial.uiEvent)

        job.cancel()
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        val fakeWeights = listOf(Weight("1", 80.toBigDecimal(), 1764523811, false, 1L))
        fakeObserveWeightsUseCase.emit(Async.Success(fakeWeights))
        fakeObserveWeightsSortingUseCase.emit(Async.Success(fakeWeightsSorting))
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
        fakeObserveWeightsSortingUseCase.emit(Async.Success(fakeWeightsSorting))
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
    fun `fab action click emits AddWeight event`() = runTest {
        fakeObserveWeightsUseCase.emit(Async.Success(listOf()))
        fakeObserveWeightsSortingUseCase.emit(Async.Success(fakeWeightsSorting))
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
        fakeObserveWeightsUseCase.emit(Async.Success(listOf()))
        fakeObserveWeightsSortingUseCase.emit(Async.Success(fakeWeightsSorting))
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
        fakeObserveWeightsUseCase.emit(Async.Success(listOf()))
        fakeObserveWeightsSortingUseCase.emit(Async.Success(fakeWeightsSorting))
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
        fakeObserveWeightsUseCase,
        fakeObserveWeightsSortingUseCase,
        mockResourcesProvider,
        savedStateHandle,
    )

    companion object {

        private val fakeWeightsSorting = listOf(
            SortMenuItem(type = WeightsSortType.BY_DATE, labelResId = R.string.sort_by_date),
            SortMenuItem(type = WeightsSortType.BY_MIN, labelResId = R.string.sort_by_min),
            SortMenuItem(type = WeightsSortType.BY_MAX, labelResId = R.string.sort_by_max),
        )

    }
}
