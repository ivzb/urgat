package com.ivzb.urgat.ui.addEditTodo

import androidx.lifecycle.SavedStateHandle
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.domain.todo.FakeCreateTodoUseCase
import com.ivzb.urgat.domain.todo.FakeGetTodoUseCase
import com.ivzb.urgat.domain.todo.FakeUpdateTodoUseCase
import com.ivzb.urgat.navigation.AddEditTodoDestination
import com.ivzb.urgat.R
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditTodoViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var getTodoUseCase: FakeGetTodoUseCase
    
    private lateinit var createTodoUseCase: FakeCreateTodoUseCase
    
    private lateinit var updateTodoUseCase: FakeUpdateTodoUseCase
    
    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var viewModel: AddEditTodoViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getTodoUseCase = FakeGetTodoUseCase()
        createTodoUseCase = FakeCreateTodoUseCase()
        updateTodoUseCase = FakeUpdateTodoUseCase()
        savedStateHandle = SavedStateHandle()

        viewModel = createViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init without id does not load todo`() = runTest {
        assertNull(getTodoUseCase.calledWithId)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `init with id loads todo`() = runTest {
        val todoId = "123"
        val todo = Todo(
            id = todoId,
            title = "Hello",
            description = "World",
            isCompleted = false
        )

        getTodoUseCase.toReturn = todo
        savedStateHandle[AddEditTodoDestination.Args.ARG_ID] = "123"

        val viewModel = createViewModel()

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Hello", state.title)
        assertEquals("World", state.description)
        assertFalse(state.isLoading)
        assertEquals("123", getTodoUseCase.calledWithId)
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
    fun `saveTodo creates new todo`() = runTest {
        viewModel.onIntent(AddEditTodoIntent.UpdateTitle("Title"))
        viewModel.onIntent(AddEditTodoIntent.UpdateDescription("Desc"))
        viewModel.onIntent(AddEditTodoIntent.SaveTodo)

        advanceUntilIdle()

        val (title, description) = createTodoUseCase.created
        assertEquals("Title", title)
        assertEquals("Desc", description)
        assertTrue(viewModel.uiState.value.isTodoSaved)
    }

    @Test
    fun `saveTodo updates existing todo`() = runTest {
        val todoId = "abc"
        savedStateHandle[AddEditTodoDestination.Args.ARG_ID] = todoId

        val todo = Todo(
            id = todoId,
            title = "Old",
            description = "Desc",
            isCompleted = false
        )
        getTodoUseCase.toReturn = todo

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

        assertTrue(viewModel.uiState.value.isTodoSaved)
        val (id, title, description) = updateTodoUseCase.updated
        assertEquals(todoId, id)
        assertEquals("Updated Title", title)
        assertEquals("Updated Desc", description)
    }

    @Test
    fun `userMessageShown clears message`() = runTest {
        viewModel.onIntent(AddEditTodoIntent.SaveTodo)
        assertNotNull(viewModel.uiState.value.userMessage)

        viewModel.onIntent(AddEditTodoIntent.UserMessageShown)
        assertNull(viewModel.uiState.value.userMessage)
    }

    private fun createViewModel() = AddEditTodoViewModel(
        getTodoUseCase,
        createTodoUseCase,
        updateTodoUseCase,
        savedStateHandle
    )
}
