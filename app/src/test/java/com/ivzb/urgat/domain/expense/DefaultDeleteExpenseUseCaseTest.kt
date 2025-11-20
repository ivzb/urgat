package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.data.expense.ExpensesRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultDeleteExpenseUseCaseTest {

    private lateinit var repository: ExpensesRepository
    private lateinit var deleteUseCase: DeleteExpenseUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true) // relaxed to ignore unmocked calls
        deleteUseCase = DefaultDeleteExpenseUseCase(repository)
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
