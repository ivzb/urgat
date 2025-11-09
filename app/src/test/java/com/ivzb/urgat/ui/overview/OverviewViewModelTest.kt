package com.ivzb.urgat.ui.overview

import com.ivzb.urgat.R
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.domain.expenses.FakeObserveExpensesUseCase
import com.ivzb.urgat.domain.notes.FakeObserveNotesUseCase
import com.ivzb.urgat.domain.todo.FakeActivateTodoUseCase
import com.ivzb.urgat.domain.todo.FakeClearCompletedTodosUseCase
import com.ivzb.urgat.domain.todo.FakeCompleteTodoUseCase
import com.ivzb.urgat.domain.todo.FakeObserveTodosUseCase
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
class TodoViewModelTest {

    private lateinit var fakeObserveTodosUseCase: FakeObserveTodosUseCase
    private lateinit var fakeObserveExpensesUseCase: FakeObserveExpensesUseCase
    private lateinit var fakeObserveNotesUseCase: FakeObserveNotesUseCase
    private lateinit var fakeCompleteTodoUseCase: FakeCompleteTodoUseCase
    private lateinit var fakeActivateTodoUseCase: FakeActivateTodoUseCase
    private lateinit var fakeClearCompletedTodosUseCase: FakeClearCompletedTodosUseCase

    private lateinit var viewModel: OverviewViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        fakeObserveTodosUseCase = FakeObserveTodosUseCase()
        fakeObserveExpensesUseCase = FakeObserveExpensesUseCase()
        fakeObserveNotesUseCase = FakeObserveNotesUseCase()
        fakeCompleteTodoUseCase = FakeCompleteTodoUseCase()
        fakeActivateTodoUseCase = FakeActivateTodoUseCase()
        fakeClearCompletedTodosUseCase = FakeClearCompletedTodosUseCase()
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
        assertTrue(initial.todoItems.isEmpty())
        assertTrue(initial.expensesItems.isEmpty())
        assertTrue(initial.notesItems.isEmpty())
        assertNull(initial.uiEvent)

        job.cancel()
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        val fakeTodo = listOf(Todo("1", "Lunch"))
        fakeObserveTodosUseCase.emit(Async.Success(fakeTodo))

        val fakeExpenses = listOf(Expense("1", "Lunch"))
        fakeObserveExpensesUseCase.emit(Async.Success(fakeExpenses))

        val fakeNotes = listOf(Note("1", "Note"))
        fakeObserveNotesUseCase.emit(Async.Success(fakeNotes))

        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(fakeTodo, state.todoItems)
        assertEquals(fakeExpenses, state.expensesItems)
        assertEquals(fakeNotes, state.notesItems)
        assertFalse(state.isLoading)
        assertFalse(state.hasError)

        job.cancel()
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveTodosUseCase.emit(Async.Error(R.string.error_todos))

        val fakeExpenses = listOf(Expense("1", "Lunch"))
        fakeObserveExpensesUseCase.emit(Async.Success(fakeExpenses))

        val fakeNotes = listOf(Note("1", "Note"))
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
        val fakeTodo = listOf(Todo("2", "Coffee"))
        fakeObserveTodosUseCase.emit(Async.Success(fakeTodo))
        fakeObserveExpensesUseCase.emit(Async.Success(listOf()))
        fakeObserveNotesUseCase.emit(Async.Success(listOf()))
        viewModel = createViewModel()

        viewModel.onIntent(OverviewIntent.SearchQueryChanged("coffee"))

        advanceUntilIdle()

        assertEquals("coffee", fakeObserveTodosUseCase.lastQuery)
        assertEquals("coffee", fakeObserveExpensesUseCase.lastQuery)
        assertEquals("coffee", fakeObserveNotesUseCase.lastQuery)
    }

    @Test
    fun `completeTodo calls completeUseCase`() = runTest {
        val todo = Todo("2", "Coffee")

        viewModel = createViewModel()

        viewModel.onIntent(OverviewIntent.CompleteTodo(todo, completed = true))
        advanceUntilIdle()

        assertEquals(todo.id, fakeCompleteTodoUseCase.completedId)
    }

    @Test
    fun `activateTodo calls activateUseCase`() = runTest {
        val todo = Todo("2", "Coffee")

        viewModel = createViewModel()

        viewModel.onIntent(OverviewIntent.CompleteTodo(todo, completed = false))
        advanceUntilIdle()

        assertEquals(todo.id, fakeActivateTodoUseCase.activatedId)
    }

    @Test
    fun `clearCompletedTodos calls clearCompletedUseCase`() = runTest {
        viewModel = createViewModel()

        viewModel.onIntent(OverviewIntent.ClearCompletedTodos)
        advanceUntilIdle()

        assertTrue(fakeClearCompletedTodosUseCase.cleared)
    }

    @Test
    fun `header item click emits AddTodo event`() = runTest {
        `header item click emits event`(OverviewHeaderItemId.Todo, OverviewUiEvent.AddTodo)
    }

    @Test
    fun `header item click emits AddExpense event`() = runTest {
        `header item click emits event`(OverviewHeaderItemId.Expense, OverviewUiEvent.AddExpense)
    }

    @Test
    fun `header item click emits AddNote event`() = runTest {
        `header item click emits event`(OverviewHeaderItemId.Note, OverviewUiEvent.AddNote)
    }

    private fun `header item click emits event`(itemId: OverviewHeaderItemId, expectedEvent: OverviewUiEvent) = runTest {
        fakeObserveTodosUseCase.emit(Async.Success(listOf()))
        fakeObserveExpensesUseCase.emit(Async.Success(listOf()))
        fakeObserveNotesUseCase.emit(Async.Success(listOf()))
        viewModel = createViewModel()

        viewModel.onIntent(OverviewIntent.HeaderItemClick(itemId))

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val event = viewModel.uiState.value.uiEvent
        assertEquals(expectedEvent, event)

        job.cancel()
    }


    private fun createViewModel() = OverviewViewModel(
        fakeObserveTodosUseCase,
        fakeObserveExpensesUseCase,
        fakeObserveNotesUseCase,
        fakeCompleteTodoUseCase,
        fakeActivateTodoUseCase,
        fakeClearCompletedTodosUseCase
    )
}
