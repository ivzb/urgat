package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.data.todo.TodoRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultDeleteTodoUseCaseTest {

    private lateinit var repository: TodoRepository
    private lateinit var deleteUseCase: DeleteTodoUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true) // relaxed to ignore unmocked calls
        deleteUseCase = DefaultDeleteTodoUseCase(repository)
    }

    @Test
    fun `invoke should call repository delete with correct parameters`() = runTest {
        val id = "123"

        deleteUseCase(id)

        coVerify(exactly = 1) {
            repository.delete(id)
        }
    }
}
