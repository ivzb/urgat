package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.data.todo.TodoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DefaultGetTodoUseCaseTest {

    private lateinit var todoRepository: TodoRepository
    private lateinit var getTodoUseCase: GetTodoUseCase

    @Before
    fun setUp() {
        todoRepository = mockk()
        getTodoUseCase = DefaultGetTodoUseCase(todoRepository)
    }

    @Test
    fun `invoke should return todo when repository returns todo`() = runTest {
        val id = "123"
        val expectedTodo = Todo(id, "Todo")
        coEvery { todoRepository.get(id) } returns expectedTodo

        val result = getTodoUseCase(id)

        assertEquals(expectedTodo, result)
        coVerify(exactly = 1) { todoRepository.get(id) }
    }

    @Test
    fun `invoke should return null when repository returns null`() = runTest {
        val id = "999"
        coEvery { todoRepository.get(id) } returns null

        val result = getTodoUseCase(id)

        assertNull(result)
        coVerify(exactly = 1) { todoRepository.get(id) }
    }
}