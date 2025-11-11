package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.data.todo.TodoRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultCompleteTodoUseCaseTest {

    private lateinit var todoRepository: TodoRepository
    private lateinit var completeTodoUseCase: CompleteTodoUseCase

    @Before
    fun setUp() {
        todoRepository = mockk(relaxed = true) // relaxed to ignore unmocked calls
        completeTodoUseCase = DefaultCompleteTodoUseCase(todoRepository)
    }

    @Test
    fun `invoke should call repository update with correct parameters`() = runTest {
        val id = "123"

        completeTodoUseCase(id)

        coVerify(exactly = 1) {
            todoRepository.complete(id)
        }
    }
}
