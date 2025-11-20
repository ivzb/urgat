package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.data.todo.TodoRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultUpdateTodoUseCaseTest {

    private lateinit var repository: TodoRepository
    private lateinit var updateUseCase: UpdateTodoUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true) // relaxed to ignore unmocked calls
        updateUseCase = DefaultUpdateTodoUseCase(repository)
    }

    @Test
    fun `invoke should call repository update with correct parameters`() = runTest {
        val id = "123"
        val title = "Title"
        val desc = "Desc"

        updateUseCase(id, title, desc)

        coVerify(exactly = 1) {
            repository.update(id, title, desc)
        }
    }
}
