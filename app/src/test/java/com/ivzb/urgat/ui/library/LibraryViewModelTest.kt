package com.ivzb.urgat.ui.library

import com.ivzb.urgat.R
import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.data.resources.ResourcesProvider
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.domain.note.FakeObserveNotesUseCase
import com.ivzb.urgat.domain.sample.FakeSampleUseCase
import com.ivzb.urgat.domain.todo.FakeActivateTodoUseCase
import com.ivzb.urgat.domain.todo.FakeClearCompletedTodosUseCase
import com.ivzb.urgat.domain.todo.FakeCompleteTodoUseCase
import com.ivzb.urgat.domain.todo.FakeObserveTodosUseCase
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
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {

    private lateinit var fakeObserveTodosUseCase: FakeObserveTodosUseCase
    private lateinit var fakeObserveNotesUseCase: FakeObserveNotesUseCase
    private lateinit var mockResourcesProvider: ResourcesProvider
    private lateinit var fakeCompleteTodoUseCase: FakeCompleteTodoUseCase
    private lateinit var fakeActivateTodoUseCase: FakeActivateTodoUseCase
    private lateinit var fakeClearCompletedTodosUseCase: FakeClearCompletedTodosUseCase
    private lateinit var fakeSampleUseCase: FakeSampleUseCase

    private lateinit var viewModel: LibraryViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        fakeObserveTodosUseCase = FakeObserveTodosUseCase()
        fakeObserveNotesUseCase = FakeObserveNotesUseCase()
        mockResourcesProvider = mockk()
        fakeCompleteTodoUseCase = FakeCompleteTodoUseCase()
        fakeActivateTodoUseCase = FakeActivateTodoUseCase()
        fakeClearCompletedTodosUseCase = FakeClearCompletedTodosUseCase()
        fakeSampleUseCase = FakeSampleUseCase()

        every { mockResourcesProvider.getInteger(R.integer.items_limit) } returns 5
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveTodosUseCase.emit(Async.Loading)
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        val initial = viewModel.uiState.value
        assertTrue(initial.isLoading)
        assertFalse(initial.hasError)
        assertTrue(initial.todos.isEmpty())
        assertTrue(initial.notes.isEmpty())
        assertNull(initial.uiEvent)

        job.cancel()
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        val fakeTodo = listOf(Todo("1", "Lunch", "", false, false, 1L))
        fakeObserveTodosUseCase.emit(Async.Success(fakeTodo))

        val fakeNotes = listOf(Note("1", "Note", false, 1L))
        fakeObserveNotesUseCase.emit(Async.Success(fakeNotes))

        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(fakeTodo, state.todos)
        assertEquals(fakeNotes, state.notes)
        assertFalse(state.isLoading)
        assertFalse(state.hasError)

        job.cancel()
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveTodosUseCase.emit(Async.Error(R.string.error_todos))

        val fakeNotes = listOf(Note("1", "Note", false, 1L))
        fakeObserveNotesUseCase.emit(Async.Success(fakeNotes))

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
        val fakeTodo = listOf(Todo("2", "Coffee", "", false, false, 1L))
        fakeObserveTodosUseCase.emit(Async.Success(fakeTodo))
        fakeObserveNotesUseCase.emit(Async.Success(listOf()))
        viewModel = createViewModel()

        viewModel.onIntent(LibraryIntent.SearchQueryChanged("coffee"))

        advanceUntilIdle()

        assertEquals("coffee", fakeObserveTodosUseCase.lastQuery)
        assertEquals("coffee", fakeObserveNotesUseCase.lastQuery)
    }

    @Test
    fun `completeTodo calls completeUseCase`() = runTest {
        val todo = Todo("2", "Coffee", "", false, false, 1L)

        viewModel = createViewModel()

        viewModel.onIntent(LibraryIntent.CompleteTodo(todo, completed = true))
        advanceUntilIdle()

        assertEquals(todo.id, fakeCompleteTodoUseCase.completedId)
    }

    @Test
    fun `activateTodo calls activateUseCase`() = runTest {
        val todo = Todo("2", "Coffee", "", false, false, 1L)

        viewModel = createViewModel()

        viewModel.onIntent(LibraryIntent.CompleteTodo(todo, completed = false))
        advanceUntilIdle()

        assertEquals(todo.id, fakeActivateTodoUseCase.activatedId)
    }

    @Test
    fun `clearCompletedTodos calls clearCompletedUseCase`() = runTest {
        viewModel = createViewModel()

        viewModel.onIntent(LibraryIntent.ClearCompletedTodos)
        advanceUntilIdle()

        assertTrue(fakeClearCompletedTodosUseCase.cleared)
    }

    @Test
    fun `fab action click emits AddTodo event`() = runTest {
        `fab action click emits event`(LibraryFabActionId.Todo, LibraryUiEvent.AddTodo)
    }

    @Test
    fun `fab action click emits AddNote event`() = runTest {
        `fab action click emits event`(LibraryFabActionId.Note, LibraryUiEvent.AddNote)
    }

    private fun `fab action click emits event`(itemId: LibraryFabActionId, expectedEvent: LibraryUiEvent) = runTest {
        fakeObserveTodosUseCase.emit(Async.Success(listOf()))
        fakeObserveNotesUseCase.emit(Async.Success(listOf()))
        viewModel = createViewModel()

        viewModel.onIntent(LibraryIntent.FabActionClick(itemId))

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val event = viewModel.uiState.value.uiEvent
        assertEquals(expectedEvent, event)

        job.cancel()
    }


    private fun createViewModel() = LibraryViewModel(
        fakeObserveTodosUseCase,
        fakeObserveNotesUseCase,
        mockResourcesProvider,
        fakeCompleteTodoUseCase,
        fakeActivateTodoUseCase,
        fakeClearCompletedTodosUseCase,
        fakeSampleUseCase,
    )
}
