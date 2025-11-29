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

    private lateinit var repository: TodoRepository
    private lateinit var getUseCase: GetTodoUseCase

    @Before
    fun setUp() {
        repository = mockk()
        getUseCase = DefaultGetTodoUseCase(repository)
    }

    @Test
    fun `invoke should return todo when repository returns todo`() = runTest {
        val id = "123"
        val expectedTodo = Todo(id, "Todo", "", false, false, 1L)
        coEvery { repository.get(id) } returns expectedTodo

        val result = getUseCase(id)

        assertEquals(expectedTodo, result)
        coVerify(exactly = 1) { repository.get(id) }
    }

    @Test
    fun `invoke should return null when repository returns null`() = runTest {
        val id = "999"
        coEvery { repository.get(id) } returns null

        val result = getUseCase(id)

        assertNull(result)
        coVerify(exactly = 1) { repository.get(id) }
    }
}