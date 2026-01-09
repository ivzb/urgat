package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.data.expense.ExpensesRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class DefaultUpdateExpenseUseCaseTest {

    private lateinit var repository: ExpensesRepository
    private lateinit var updateUseCase: UpdateExpenseUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true) // relaxed to ignore unmocked calls
        updateUseCase = DefaultUpdateExpenseUseCase(repository)
    }

    @Test
    fun `invoke should call repository update with correct parameters`() = runTest {
        val id = "123"
        val name = "Lunch"
        val amount = BigDecimal("15.50")
        val category = "Food"
        val date = 1731268800000L
        val excludeFromAnalytics = false

        updateUseCase(id, name, amount, category, date, excludeFromAnalytics)

        coVerify(exactly = 1) {
            repository.update(id, name, amount, category, date, excludeFromAnalytics)
        }
    }
}
