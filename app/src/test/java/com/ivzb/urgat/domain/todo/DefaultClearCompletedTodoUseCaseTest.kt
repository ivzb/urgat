package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.data.todo.TodoRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultClearCompletedTodoUseCaseTest {

    private lateinit var todoRepository: TodoRepository
    private lateinit var clearCompletedTodoUseCase: ClearCompletedTodosUseCase

    @Before
    fun setUp() {
        todoRepository = mockk(relaxed = true) // relaxed to ignore unmocked calls
        clearCompletedTodoUseCase = DefaultClearCompletedTodosUseCase(todoRepository)
    }

    @Test
    fun `invoke should call repository update with correct parameters`() = runTest {
        clearCompletedTodoUseCase()

        coVerify(exactly = 1) {
            todoRepository.clearCompleted()
        }
    }
}
