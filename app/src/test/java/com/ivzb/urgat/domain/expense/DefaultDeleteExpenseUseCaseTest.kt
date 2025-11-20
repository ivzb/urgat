package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.data.expense.ExpensesRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultDeleteExpenseUseCaseTest {

    private lateinit var expensesRepository: ExpensesRepository
    private lateinit var deleteExpenseUseCase: DeleteExpenseUseCase

    @Before
    fun setUp() {
        expensesRepository = mockk(relaxed = true) // relaxed to ignore unmocked calls
        deleteExpenseUseCase = DefaultDeleteExpenseUseCase(expensesRepository)
    }

    @Test
    fun `invoke should call repository delete with correct parameters`() = runTest {
        val id = "123"

        deleteExpenseUseCase(id)

        coVerify(exactly = 1) {
            expensesRepository.delete(id)
        }
    }
}
