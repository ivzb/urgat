package com.ivzb.urgat.ui.todo

import androidx.lifecycle.SavedStateHandle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.domain.todo.FakeActivateTodoUseCase
import com.ivzb.urgat.domain.todo.FakeClearCompletedTodosUseCase
import com.ivzb.urgat.domain.todo.FakeCompleteTodoUseCase
import com.ivzb.urgat.domain.todo.FakeObserveTodosFilteringUseCase
import com.ivzb.urgat.domain.todo.FakeObserveTodosUseCase
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class TodoViewModelTest {

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var fakeObserveTodosUseCase: FakeObserveTodosUseCase
    private lateinit var fakeObserveTodosFilteringUseCase: FakeObserveTodosFilteringUseCase
    private lateinit var fakeCompleteTodoUseCase: FakeCompleteTodoUseCase
    private lateinit var fakeActivateTodoUseCase: FakeActivateTodoUseCase
    private lateinit var fakeClearCompletedTodosUseCase: FakeClearCompletedTodosUseCase

    private lateinit var viewModel: TodoViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        savedStateHandle = SavedStateHandle(mapOf(TODOS_FILTER_SAVED_STATE_KEY to null))
        fakeObserveTodosUseCase = FakeObserveTodosUseCase()
        fakeCompleteTodoUseCase = FakeCompleteTodoUseCase()
        fakeActivateTodoUseCase = FakeActivateTodoUseCase()
        fakeClearCompletedTodosUseCase = FakeClearCompletedTodosUseCase()
        fakeObserveTodosFilteringUseCase = FakeObserveTodosFilteringUseCase()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveTodosUseCase.emit(Async.Loading)
        fakeObserveTodosFilteringUseCase.emit(Async.Loading)
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        val initial = viewModel.uiState.value
        assertTrue(initial.isLoading)
        assertFalse(initial.hasError)
        assertTrue(initial.items.isEmpty())

        job.cancel()
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        val fakeTodo = listOf(Todo("1", "Lunch"))
        val fakeTodoFiltering = listOf<TodoFilterMenuItem>(
            FilterMenuItem(type = TodoFilterType.ALL_TODOS, labelResId = R.string.filter_all, size = 1),
            FilterMenuItem(type = TodoFilterType.ACTIVE_TODOS, labelResId = R.string.filter_active, size = 1),
            FilterMenuItem(type = TodoFilterType.COMPLETED_TODOS, labelResId = R.string.filter_completed, size = 0),
        )

        fakeObserveTodosUseCase.emit(Async.Success(fakeTodo))
        fakeObserveTodosFilteringUseCase.emit(Async.Success(fakeTodoFiltering))
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(fakeTodo, state.items)
        assertEquals(fakeTodoFiltering, state.filteringUiInfo.filterMenuItems)
        assertFalse(state.isLoading)
        assertFalse(state.hasError)

        job.cancel()
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveTodosUseCase.emit(Async.Error(R.string.error_todos))
        fakeObserveTodosFilteringUseCase.emit(Async.Success(listOf()))
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
        val fakeTodo = listOf(Todo("2", "Coffee"))
        fakeObserveTodosUseCase.emit(Async.Success(fakeTodo))
        fakeObserveTodosFilteringUseCase.emit(Async.Success(listOf()))
        viewModel = createViewModel()

        viewModel.onIntent(TodoIntent.SearchQueryChanged("coffee"))

        advanceUntilIdle()

        assertEquals("coffee", fakeObserveTodosUseCase.lastQuery)
    }

    @Test
    fun `setFiltering updates saved state handle`() = runTest {
        fakeObserveTodosUseCase.emit(Async.Success(listOf()))
        fakeObserveTodosFilteringUseCase.emit(Async.Success(listOf()))
        viewModel = createViewModel()

        val expected = FilterMenuItem<TodoFilterType>(type = TodoFilterType.ACTIVE_TODOS, labelResId = R.string.filter_active, size = 1)

        viewModel.onIntent(TodoIntent.SetFiltering(expected))
        advanceUntilIdle()

        assertEquals(
            expected,
            savedStateHandle[TODOS_FILTER_SAVED_STATE_KEY]
        )
    }

    @Test
    fun `completeTodo calls completeUseCase`() = runTest {
        val todo = Todo("2", "Coffee")

        viewModel = createViewModel()

        viewModel.onIntent(TodoIntent.CompleteTodo(todo, completed = true))
        advanceUntilIdle()

        assertEquals(todo.id, fakeCompleteTodoUseCase.completedId)
    }

    @Test
    fun `activateTodo calls activateUseCase`() = runTest {
        val todo = Todo("2", "Coffee")

        viewModel = createViewModel()

        viewModel.onIntent(TodoIntent.CompleteTodo(todo, completed = false))
        advanceUntilIdle()

        assertEquals(todo.id, fakeActivateTodoUseCase.activatedId)
    }

    @Test
    fun `clearCompletedTodos calls clearCompletedUseCase`() = runTest {
        viewModel = createViewModel()

        viewModel.onIntent(TodoIntent.ClearCompletedTodos)
        advanceUntilIdle()

        assertTrue(fakeClearCompletedTodosUseCase.cleared)
    }

    private fun createViewModel() = TodoViewModel(
        fakeObserveTodosUseCase,
        fakeObserveTodosFilteringUseCase,
        fakeCompleteTodoUseCase,
        fakeActivateTodoUseCase,
        fakeClearCompletedTodosUseCase,
        savedStateHandle,
    )
}