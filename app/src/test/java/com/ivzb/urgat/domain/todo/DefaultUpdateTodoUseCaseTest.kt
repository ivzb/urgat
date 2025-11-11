package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.data.todo.TodoRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultUpdateTodoUseCaseTest {

    private lateinit var todoRepository: TodoRepository
    private lateinit var updateTodoUseCase: UpdateTodoUseCase

    @Before
    fun setUp() {
        todoRepository = mockk(relaxed = true) // relaxed to ignore unmocked calls
        updateTodoUseCase = DefaultUpdateTodoUseCase(todoRepository)
    }

    @Test
    fun `invoke should call repository update with correct parameters`() = runTest {
        val id = "123"
        val title = "Title"
        val desc = "Desc"

        updateTodoUseCase(id, title, desc)

        coVerify(exactly = 1) {
            todoRepository.update(id, title, desc)
        }
    }
}
