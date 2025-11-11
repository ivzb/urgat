package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.data.todo.TodoRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultCreateTodoUseCaseTest {

    private lateinit var todoRepository: TodoRepository
    private lateinit var createTodoUseCase: CreateTodoUseCase

    @Before
    fun setUp() {
        todoRepository = mockk(relaxed = true)
        createTodoUseCase = DefaultCreateTodoUseCase(todoRepository)
    }

    @Test
    fun `invoke should call repository create with correct parameters`() = runTest {
        val title = "Todo"
        val desc = "Desc"

        createTodoUseCase(title, desc)

        coVerify(exactly = 1) {
            todoRepository.create(title, desc)
        }
    }
}