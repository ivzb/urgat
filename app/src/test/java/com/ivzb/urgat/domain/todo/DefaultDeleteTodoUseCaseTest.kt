package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.data.todo.TodoRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultDeleteTodoUseCaseTest {

    private lateinit var todoRepository: TodoRepository
    private lateinit var deleteTodoUseCase: DeleteTodoUseCase

    @Before
    fun setUp() {
        todoRepository = mockk(relaxed = true) // relaxed to ignore unmocked calls
        deleteTodoUseCase = DefaultDeleteTodoUseCase(todoRepository)
    }

    @Test
    fun `invoke should call repository delete with correct parameters`() = runTest {
        val id = "123"

        deleteTodoUseCase(id)

        coVerify(exactly = 1) {
            todoRepository.delete(id)
        }
    }
}
