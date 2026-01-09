package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.data.expense.ExpensesRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class DefaultCreateExpenseUseCaseTest {

    private lateinit var repository: ExpensesRepository
    private lateinit var createUseCase: CreateExpenseUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        createUseCase = DefaultCreateExpenseUseCase(repository)
    }

    @Test
    fun `invoke should call repository create with correct parameters`() = runTest {
        val name = "Groceries"
        val amount = BigDecimal("45.90")
        val category = "Food"
        val date = 1731268800000L
        val excludeFromAnalytics = false

        createUseCase(name, amount, category, date, excludeFromAnalytics)

        coVerify(exactly = 1) {
            repository.create(name, amount, category, date, excludeFromAnalytics)
        }
    }
}