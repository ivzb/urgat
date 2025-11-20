package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.data.todo.TodoRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultCreateTodoUseCaseTest {

    private lateinit var repository: TodoRepository
    private lateinit var createUseCase: CreateTodoUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        createUseCase = DefaultCreateTodoUseCase(repository)
    }

    @Test
    fun `invoke should call repository create with correct parameters`() = runTest {
        val title = "Todo"
        val desc = "Desc"

        createUseCase(title, desc)

        coVerify(exactly = 1) {
            repository.create(title, desc)
        }
    }
}