package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.data.expense.ExpensesRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class DefaultCreateExpenseUseCaseTest {

    private lateinit var expensesRepository: ExpensesRepository
    private lateinit var createExpenseUseCase: CreateExpenseUseCase

    @Before
    fun setUp() {
        expensesRepository = mockk(relaxed = true)
        createExpenseUseCase = DefaultCreateExpenseUseCase(expensesRepository)
    }

    @Test
    fun `invoke should call repository create with correct parameters`() = runTest {
        val name = "Groceries"
        val amount = BigDecimal("45.90")
        val category = "Food"
        val date = 1731268800000L

        createExpenseUseCase(name, amount, category, date)

        coVerify(exactly = 1) {
            expensesRepository.create(name, amount, category, date)
        }
    }
}