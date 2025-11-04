package com.ivzb.urgat.ui.addEditTodo

import androidx.lifecycle.SavedStateHandle
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.data.FakeCreateTodoUseCase
import com.ivzb.urgat.data.FakeGetTodoUseCase
import com.ivzb.urgat.data.FakeUpdateTodoUseCase
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

    private val dispatcher = StandardTestDispatcher()
    
    private lateinit var getTodoUseCase: FakeGetTodoUseCase
    
    private lateinit var createTodoUseCase: FakeCreateTodoUseCase
    
    private lateinit var updateTodoUseCase: FakeUpdateTodoUseCase
    
    private lateinit var savedStateHandle: SavedStateHandle

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        getTodoUseCase = FakeGetTodoUseCase()
        createTodoUseCase = FakeCreateTodoUseCase()
        updateTodoUseCase = FakeUpdateTodoUseCase()
        savedStateHandle = SavedStateHandle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init without id does not load todo`() = runTest {
        val viewModel = createViewModel()

        assertNull(getTodoUseCase.calledWithId)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `init with id loads todo`() = runTest {
        val todo = Todo("123", "Hello", "World", false)

        getTodoUseCase = FakeGetTodoUseCase(todo)
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
        val viewModel = createViewModel()
        viewModel.onIntent(AddEditTodoIntent.UpdateTitle("New Title"))
        assertEquals("New Title", viewModel.uiState.value.title)
    }

    @Test
    fun `updateDescription updates ui state`() = runTest {
        val viewModel = createViewModel()
        viewModel.onIntent(AddEditTodoIntent.UpdateDescription("Desc"))
        assertEquals("Desc", viewModel.uiState.value.description)
    }

    @Test
    fun `saveTodo with empty fields shows message`() = runTest {
        val viewModel = createViewModel()
        viewModel.onIntent(AddEditTodoIntent.SaveTodo)
        assertEquals(R.string.empty_todo_message, viewModel.uiState.value.userMessage)
    }

    @Test
    fun `createNewTodo calls create usecase and sets isTodoSaved`() = runTest {
        val viewModel = createViewModel()

        viewModel.onIntent(AddEditTodoIntent.UpdateTitle("Title"))
        viewModel.onIntent(AddEditTodoIntent.UpdateDescription("Desc"))
        viewModel.onIntent(AddEditTodoIntent.SaveTodo)

        advanceUntilIdle()

        assertEquals(listOf("Title" to "Desc"), createTodoUseCase.created)
        assertTrue(viewModel.uiState.value.isTodoSaved)
    }

    @Test
    fun `updateTodo calls update usecase and sets isTodoSaved`() = runTest {
        val todoId = "abc"
        savedStateHandle[AddEditTodoDestination.Args.ARG_ID] = todoId

        val todo = Todo(todoId, "Old", "Desc", false)
        getTodoUseCase = FakeGetTodoUseCase(todo)

        val viewModel = createViewModel()

        advanceUntilIdle()

        viewModel.onIntent(AddEditTodoIntent.UpdateTitle("Updated"))
        viewModel.onIntent(AddEditTodoIntent.SaveTodo)

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isTodoSaved)
        assertEquals(listOf(Triple(todoId, "Updated", "Desc")), updateTodoUseCase.updated)
    }

    @Test
    fun `userMessageShown clears message`() = runTest {
        val viewModel = createViewModel()
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
