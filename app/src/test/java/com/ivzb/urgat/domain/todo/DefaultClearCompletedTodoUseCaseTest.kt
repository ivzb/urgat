package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.data.todo.TodoRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultClearCompletedTodoUseCaseTest {

    private lateinit var repository: TodoRepository
    private lateinit var clearTodoUseCase: ClearCompletedTodosUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true) // relaxed to ignore unmocked calls
        clearTodoUseCase = DefaultClearCompletedTodosUseCase(repository)
    }

    @Test
    fun `invoke should call repository update with correct parameters`() = runTest {
        clearTodoUseCase()

        coVerify(exactly = 1) {
            repository.clearCompleted()
        }
    }
}
