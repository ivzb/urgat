package com.ivzb.urgat.ui.addEditTodo

import androidx.lifecycle.SavedStateHandle
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.domain.todo.FakeCreateTodoUseCase
import com.ivzb.urgat.domain.todo.FakeGetTodoUseCase
import com.ivzb.urgat.domain.todo.FakeUpdateTodoUseCase
import com.ivzb.urgat.navigation.AddEditTodoDestination
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.todo.FakeDeleteTodoUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import kotlin.collections.component1
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditTodoViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var getUseCase: FakeGetTodoUseCase
    
    private lateinit var createUseCase: FakeCreateTodoUseCase
    
    private lateinit var updateUseCase: FakeUpdateTodoUseCase

    private lateinit var deleteUseCase: FakeDeleteTodoUseCase

    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var viewModel: AddEditTodoViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getUseCase = FakeGetTodoUseCase()
        createUseCase = FakeCreateTodoUseCase()
        updateUseCase = FakeUpdateTodoUseCase()
        deleteUseCase = FakeDeleteTodoUseCase()
        savedStateHandle = SavedStateHandle()

        viewModel = createViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init without id does not load todo`() = runTest {
        assertNull(getUseCase.calledWithId)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `init with id loads todo`() = runTest {
        val todoId = "123"
        val todo = Todo(
            id = todoId,
            title = "Hello",
            description = "World",
            isCompleted = false,
            isDeleted = false,
            createdAt = 1L,
        )

        getUseCase.toReturn = todo
        savedStateHandle[AddEditTodoDestination.Args.ARG_ID] = "123"

        val viewModel = createViewModel()

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Hello", state.title)
        assertEquals("World", state.description)
        assertFalse(state.isLoading)
        assertEquals("123", getUseCase.calledWithId)
        assertEquals(true, state.withDelete)
        assertEquals(false, state.shouldNavigateBack)
    }

    @Test
    fun `updateTitle updates ui state`() = runTest {
        viewModel.onIntent(AddEditTodoIntent.UpdateTitle("New Title"))
        assertEquals("New Title", viewModel.uiState.value.title)
    }

    @Test
    fun `updateDescription updates ui state`() = runTest {
        viewModel.onIntent(AddEditTodoIntent.UpdateDescription("Desc"))
        assertEquals("Desc", viewModel.uiState.value.description)
    }

    @Test
    fun `saveTodo with empty fields shows message`() = runTest {
        viewModel.onIntent(AddEditTodoIntent.SaveTodo)
        assertEquals(R.string.empty_todo_message, viewModel.uiState.value.userMessage)
    }

    @Test
    fun `saveTodo with spaces shows message`() = runTest {
        viewModel.onIntent(AddEditTodoIntent.UpdateTitle(" "))
        viewModel.onIntent(AddEditTodoIntent.UpdateDescription(" "))
        viewModel.onIntent(AddEditTodoIntent.SaveTodo)
        assertEquals(R.string.empty_todo_message, viewModel.uiState.value.userMessage)
    }

    @Test
    fun `saveTodo creates new todo`() = runTest {
        viewModel.onIntent(AddEditTodoIntent.UpdateTitle("Title"))
        viewModel.onIntent(AddEditTodoIntent.UpdateDescription("Desc"))
        viewModel.onIntent(AddEditTodoIntent.SaveTodo)

        advanceUntilIdle()

        val (title, description) = createUseCase.created
        assertEquals("Title", title)
        assertEquals("Desc", description)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveTodo creates new todo trimmed`() = runTest {
        viewModel.onIntent(AddEditTodoIntent.UpdateTitle(" Title "))
        viewModel.onIntent(AddEditTodoIntent.UpdateDescription(" Desc "))
        viewModel.onIntent(AddEditTodoIntent.SaveTodo)

        advanceUntilIdle()

        val (title, description) = createUseCase.created
        assertEquals("Title", title)
        assertEquals("Desc", description)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveTodo updates existing todo`() = runTest {
        val todoId = "abc"
        savedStateHandle[AddEditTodoDestination.Args.ARG_ID] = todoId

        val todo = Todo(
            id = todoId,
            title = "Old",
            description = "Desc",
            isCompleted = false,
            isDeleted = false,
            createdAt = 1L,
        )
        getUseCase.toReturn = todo

        val viewModel = createViewModel()

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(todo.title, uiState.title)
        assertEquals(todo.description, uiState.description)
        assertEquals(todo.isCompleted, uiState.isTodoCompleted)
        assertFalse(uiState.isLoading)

        viewModel.onIntent(AddEditTodoIntent.UpdateTitle("Updated Title"))
        viewModel.onIntent(AddEditTodoIntent.UpdateDescription("Updated Desc"))
        viewModel.onIntent(AddEditTodoIntent.SaveTodo)

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.shouldNavigateBack)
        val (id, title, description) = updateUseCase.updated
        assertEquals(todoId, id)
        assertEquals("Updated Title", title)
        assertEquals("Updated Desc", description)
    }

    @Test
    fun `saveTodo updates existing todo trimmed`() = runTest {
        val todoId = "abc"
        savedStateHandle[AddEditTodoDestination.Args.ARG_ID] = todoId

        val todo = Todo(
            id = todoId,
            title = "Old",
            description = "Desc",
            isCompleted = false,
            isDeleted = false,
            createdAt = 1L,
        )
        getUseCase.toReturn = todo

        val viewModel = createViewModel()

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(todo.title, uiState.title)
        assertEquals(todo.description, uiState.description)
        assertEquals(todo.isCompleted, uiState.isTodoCompleted)
        assertFalse(uiState.isLoading)

        viewModel.onIntent(AddEditTodoIntent.UpdateTitle(" Updated Title "))
        viewModel.onIntent(AddEditTodoIntent.UpdateDescription(" Updated Desc "))
        viewModel.onIntent(AddEditTodoIntent.SaveTodo)

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.shouldNavigateBack)
        val (id, title, description) = updateUseCase.updated
        assertEquals(todoId, id)
        assertEquals("Updated Title", title)
        assertEquals("Updated Desc", description)
    }

    @Test
    fun `deleteTodo deletes existing todo`() = runTest {
        val todoId = "123"
        savedStateHandle[AddEditTodoDestination.Args.ARG_ID] = todoId

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(AddEditTodoIntent.DeleteTodo)
        advanceUntilIdle()

        val (id) = deleteUseCase.deleted
        assertEquals(todoId, id)
    }

    @Test
    fun `deleteTodo deletes non-existing todo`() = runTest {
        assertFailsWith<Exception> {
            viewModel.onIntent(AddEditTodoIntent.DeleteTodo)

            val uiState = viewModel.uiState.value
            assertEquals(R.string.something_went_wrong, uiState.userMessage)
        }
    }

    @Test
    fun `userMessageShown clears message`() = runTest {
        viewModel.onIntent(AddEditTodoIntent.SaveTodo)
        assertNotNull(viewModel.uiState.value.userMessage)

        viewModel.onIntent(AddEditTodoIntent.UserMessageShown)
        assertNull(viewModel.uiState.value.userMessage)
    }

    private fun createViewModel() = AddEditTodoViewModel(
        getUseCase,
        createUseCase,
        updateUseCase,
        deleteUseCase,
        savedStateHandle
    )
}
