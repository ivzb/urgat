package com.ivzb.urgat.ui.library

import com.ivzb.urgat.R
import com.ivzb.urgat.data.library.FakeLibraryRepository
import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.domain.note.FakeObserveNotesUiStateUseCase
import com.ivzb.urgat.domain.todo.FakeActivateTodoUseCase
import com.ivzb.urgat.domain.todo.FakeClearCompletedTodosUseCase
import com.ivzb.urgat.domain.todo.FakeCompleteTodoUseCase
import com.ivzb.urgat.domain.todo.FakeObserveTodosUiStateUseCase
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.notes.NotesUiState
import com.ivzb.urgat.ui.todos.TodosFilterType
import com.ivzb.urgat.ui.todos.TodosFilteringUiInfo
import com.ivzb.urgat.ui.todos.TodosUiState
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
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {

    private lateinit var fakeObserveTodosUiStateUseCase: FakeObserveTodosUiStateUseCase
    private lateinit var fakeObserveNotesUiStateUseCase: FakeObserveNotesUiStateUseCase
    private lateinit var fakeCompleteTodoUseCase: FakeCompleteTodoUseCase
    private lateinit var fakeActivateTodoUseCase: FakeActivateTodoUseCase
    private lateinit var fakeClearCompletedTodosUseCase: FakeClearCompletedTodosUseCase
    private lateinit var fakeLibraryRepository: FakeLibraryRepository

    private lateinit var viewModel: LibraryViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        fakeObserveTodosUiStateUseCase = FakeObserveTodosUiStateUseCase()
        fakeObserveNotesUiStateUseCase = FakeObserveNotesUiStateUseCase()
        fakeCompleteTodoUseCase = FakeCompleteTodoUseCase()
        fakeActivateTodoUseCase = FakeActivateTodoUseCase()
        fakeClearCompletedTodosUseCase = FakeClearCompletedTodosUseCase()
        fakeLibraryRepository = FakeLibraryRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveTodosUiStateUseCase.emit(Async.Loading)
        fakeObserveNotesUiStateUseCase.emit(Async.Loading)
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        val initial = viewModel.uiState.value
        assertTrue(initial.isLoading)
        assertFalse(initial.hasError)
        assertTrue(initial.todosUiState.todos.isEmpty())
        assertTrue(initial.notesUiState.notes.isEmpty())
        assertNull(initial.uiEvent)

        job.cancel()
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        emitSuccessData(showFilterBadge = false)
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(fakeTodos, state.todosUiState.todos)
        assertEquals(fakeTodosFiltering, state.todosUiState.filteringUiInfo.filterMenuItems)
        assertFalse(state.todosUiState.filteringUiInfo.showFilterBadge)
        assertEquals(fakeNotes, state.notesUiState.notes)
        assertFalse(state.isLoading)
        assertFalse(state.hasError)

        job.cancel()
    }

    @Test
    fun `emits success state when use case returns data with filter badge`() = runTest {
        emitSuccessData(showFilterBadge = true)
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(fakeTodos, state.todosUiState.todos)
        assertEquals(fakeTodosFiltering, state.todosUiState.filteringUiInfo.filterMenuItems)
        assertTrue(state.todosUiState.filteringUiInfo.showFilterBadge)
        assertEquals(fakeNotes, state.notesUiState.notes)
        assertFalse(state.isLoading)
        assertFalse(state.hasError)

        job.cancel()
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveTodosUiStateUseCase.emit(Async.Error)
        fakeObserveNotesUiStateUseCase.emit(Async.Success(NotesUiState(notes = fakeNotes)))

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
        val fakeTodo = listOf(Todo("2", "Coffee", false, false, 1L))
        fakeObserveTodosUiStateUseCase.emit(Async.Success(TodosUiState(todos = fakeTodo)))
        fakeObserveNotesUiStateUseCase.emit(Async.Success(NotesUiState()))
        viewModel = createViewModel()

        viewModel.onIntent(LibraryIntent.SearchQueryChanged("coffee"))

        advanceUntilIdle()

        assertEquals("coffee", fakeObserveTodosUiStateUseCase.lastQuery)
        assertEquals("coffee", fakeObserveNotesUiStateUseCase.lastQuery)
    }

    @Test
    fun `completeTodo calls completeUseCase`() = runTest {
        val todo = Todo("2", "Coffee", false, false, 1L)

        viewModel = createViewModel()

        viewModel.onIntent(LibraryIntent.CompleteTodo(todo, completed = true))
        advanceUntilIdle()

        assertEquals(todo.id, fakeCompleteTodoUseCase.completedId)
    }

    @Test
    fun `activateTodo calls activateUseCase`() = runTest {
        val todo = Todo("2", "Coffee", false, false, 1L)

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
        `fab action click emits event`(LibraryPage.Todos, LibraryUiEvent.AddTodo)
    }

    @Test
    fun `fab action click emits AddNote event`() = runTest {
        `fab action click emits event`(LibraryPage.Notes, LibraryUiEvent.AddNote)
    }

    private fun `fab action click emits event`(page: Page, expectedEvent: LibraryUiEvent) = runTest {
        fakeObserveTodosUiStateUseCase.emit(Async.Success(TodosUiState()))
        fakeObserveNotesUiStateUseCase.emit(Async.Success(NotesUiState()))
        viewModel = createViewModel()

        viewModel.onIntent(LibraryIntent.FabActionClick(page))

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val event = viewModel.uiState.value.uiEvent
        assertEquals(expectedEvent, event)

        job.cancel()
    }

    @Test
    fun `SetTodosFiltering updates library repository`() = runTest {
        viewModel = createViewModel()

        val expected = TodosFilterType.COMPLETED_TODOS

        viewModel.onIntent(LibraryIntent.SetTodosFiltering(expected))
        advanceUntilIdle()

        assertEquals(expected, fakeLibraryRepository.todosFilterTypeFlow.value)
    }

    @Test
    fun `resetFilters resets search query to empty`() = runTest {
        viewModel = createViewModel()

        viewModel.onIntent(LibraryIntent.SearchQueryChanged("coffee"))
        advanceUntilIdle()
        assertEquals("coffee", fakeLibraryRepository.searchQueryFlow.value)

        viewModel.onIntent(LibraryIntent.ResetFilters)
        advanceUntilIdle()

        assertEquals("", fakeLibraryRepository.searchQueryFlow.value)
    }

    @Test
    fun `resetFilters resets todos filter type to default`() = runTest {
        viewModel = createViewModel()

        viewModel.onIntent(LibraryIntent.SetTodosFiltering(TodosFilterType.COMPLETED_TODOS))
        advanceUntilIdle()
        assertEquals(TodosFilterType.COMPLETED_TODOS, fakeLibraryRepository.todosFilterTypeFlow.value)

        viewModel.onIntent(LibraryIntent.ResetFilters)
        advanceUntilIdle()

        assertEquals(TodosFilterType.ALL_TODOS, fakeLibraryRepository.todosFilterTypeFlow.value)
    }

    @Test
    fun `resetFilters resets search query and todos filter type at once`() = runTest {
        viewModel = createViewModel()

        viewModel.onIntent(LibraryIntent.SearchQueryChanged("coffee"))
        viewModel.onIntent(LibraryIntent.SetTodosFiltering(TodosFilterType.COMPLETED_TODOS))
        advanceUntilIdle()

        assertEquals("coffee", fakeLibraryRepository.searchQueryFlow.value)
        assertEquals(TodosFilterType.COMPLETED_TODOS, fakeLibraryRepository.todosFilterTypeFlow.value)

        viewModel.onIntent(LibraryIntent.ResetFilters)
        advanceUntilIdle()

        assertEquals("", fakeLibraryRepository.searchQueryFlow.value)
        assertEquals(TodosFilterType.ALL_TODOS, fakeLibraryRepository.todosFilterTypeFlow.value)
    }

    @Test
    fun `resetFilters clears search query in ui state`() = runTest {
        viewModel = createViewModel()

        viewModel.onIntent(LibraryIntent.SearchQueryChanged("coffee"))
        advanceUntilIdle()
        assertEquals("coffee", fakeLibraryRepository.searchQueryFlow.value)

        viewModel.onIntent(LibraryIntent.ResetFilters)
        advanceUntilIdle()

        assertEquals("", fakeLibraryRepository.searchQueryFlow.value)
    }

    private fun createViewModel() = LibraryViewModel(
        fakeObserveTodosUiStateUseCase,
        fakeObserveNotesUiStateUseCase,
        fakeCompleteTodoUseCase,
        fakeActivateTodoUseCase,
        fakeClearCompletedTodosUseCase,
        fakeLibraryRepository,
    )

    private fun emitSuccessData(showFilterBadge: Boolean) {
        fakeObserveTodosUiStateUseCase.emit(Async.Success(TodosUiState(
            todos = fakeTodos,
            filteringUiInfo = TodosFilteringUiInfo(
                filterMenuItems = fakeTodosFiltering,
                showFilterBadge = showFilterBadge,
            ),
        )))
        fakeObserveNotesUiStateUseCase.emit(Async.Success(NotesUiState(notes = fakeNotes)))
    }

    companion object {
        private val fakeTodos = listOf(Todo("1", "Lunch", false, false, 1L))
        private val fakeTodosFiltering = listOf(
            FilterMenuItem(type = TodosFilterType.ALL_TODOS, labelResId = R.string.filter_all, size = 1),
            FilterMenuItem(type = TodosFilterType.ACTIVE_TODOS, labelResId = R.string.filter_active, size = 1),
            FilterMenuItem(type = TodosFilterType.COMPLETED_TODOS, labelResId = R.string.filter_completed, size = 0),
        )
        private val fakeNotes = listOf(Note("1", "Note", false, 1L))
    }
}
