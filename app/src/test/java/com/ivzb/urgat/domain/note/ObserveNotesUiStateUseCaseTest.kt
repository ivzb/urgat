package com.ivzb.urgat.domain.note

import com.ivzb.urgat.R
import com.ivzb.urgat.data.note.Note
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
class ObserveNotesUiStateUseCaseTest {

    private lateinit var fakeObserveNotesUseCase: FakeObserveNotesUseCase

    private lateinit var useCase: ObserveNotesUiStateUseCase

    @Before
    fun setUp() {
        fakeObserveNotesUseCase = FakeObserveNotesUseCase()

        useCase = DefaultObserveNotesUiStateUseCase(
            fakeObserveNotesUseCase,
        )
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveNotesUseCase.emit(Async.Loading)

        val searchQueryFlow = flowOf("")

        val results = useCase(searchQueryFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.isLoading)
        assertFalse(uiState.hasError)
        assertTrue(uiState.notes.isEmpty())
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")

        val results = useCase(searchQueryFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertEquals(fakeNotes, uiState.notes)
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveNotesUseCase.emit(Async.Error)

        val searchQueryFlow = flowOf("")

        val results = useCase(searchQueryFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.hasError)
        assertFalse(uiState.isLoading)
    }

    @Test
    fun `hasAppliedFilters is false when no filters are applied`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")

        val results = useCase(searchQueryFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertFalse(uiState.filteringUiInfo.hasAppliedFilters)
        assertEquals("", uiState.filteringUiInfo.searchQuery)
        assertEquals(R.string.no_notes, uiState.filteringUiInfo.emptyLabel)
        assertEquals(R.string.no_notes_message, uiState.filteringUiInfo.emptyMessage)
        assertEquals(R.string.add_note, uiState.filteringUiInfo.emptyButtonLabel)
    }

    @Test
    fun `hasAppliedFilters is true when search query is not empty`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("reminder")

        val results = useCase(searchQueryFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.filteringUiInfo.hasAppliedFilters)
        assertEquals("reminder", uiState.filteringUiInfo.searchQuery)
        assertEquals(R.string.no_notes_search, uiState.filteringUiInfo.emptyLabel)
        assertEquals(R.string.no_notes_search_message, uiState.filteringUiInfo.emptyMessage)
        assertEquals(R.string.reset_filters, uiState.filteringUiInfo.emptyButtonLabel)
    }

    private fun emitSuccessData() {
        fakeObserveNotesUseCase.emit(Async.Success(fakeNotes))
    }

    companion object {
        private val fakeNotes = listOf(
            Note("1", "Buy groceries", false, 1L)
        )
    }

}

