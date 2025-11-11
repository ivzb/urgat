package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.data.todo.TodoRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultActivateTodoUseCaseTest {

    private lateinit var todoRepository: TodoRepository
    private lateinit var activateTodoUseCase: ActivateTodoUseCase

    @Before
    fun setUp() {
        todoRepository = mockk(relaxed = true) // relaxed to ignore unmocked calls
        activateTodoUseCase = DefaultActivateTodoUseCase(todoRepository)
    }

    @Test
    fun `invoke should call repository update with correct parameters`() = runTest {
        val id = "123"

        activateTodoUseCase(id)

        coVerify(exactly = 1) {
            todoRepository.activate(id)
        }
    }
}
