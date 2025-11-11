package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.data.expense.ExpensesRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class DefaultUpdateExpenseUseCaseTest {

    private lateinit var expensesRepository: ExpensesRepository
    private lateinit var updateExpenseUseCase: UpdateExpenseUseCase

    @Before
    fun setUp() {
        expensesRepository = mockk(relaxed = true) // relaxed to ignore unmocked calls
        updateExpenseUseCase = DefaultUpdateExpenseUseCase(expensesRepository)
    }

    @Test
    fun `invoke should call repository update with correct parameters`() = runTest {
        val id = "123"
        val name = "Lunch"
        val amount = BigDecimal("15.50")
        val category = "Food"
        val date = 1731268800000L

        updateExpenseUseCase(id, name, amount, category, date)

        coVerify(exactly = 1) {
            expensesRepository.update(id, name, amount, category, date)
        }
    }
}
