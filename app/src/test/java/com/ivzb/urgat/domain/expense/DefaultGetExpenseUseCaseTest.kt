package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpensesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DefaultGetExpenseUseCaseTest {

    private lateinit var repository: ExpensesRepository
    private lateinit var getUseCase: GetExpenseUseCase

    @Before
    fun setUp() {
        repository = mockk()
        getUseCase = DefaultGetExpenseUseCase(repository)
    }

    @Test
    fun `invoke should return expense when repository returns expense`() = runTest {
        val id = "123"
        val expectedExpense = Expense(id, "Coffee", BigDecimal("3.50"), "Food", 1731268800000L, false, false, 1L)
        coEvery { repository.get(id) } returns expectedExpense

        val result = getUseCase(id)

        assertEquals(expectedExpense, result)
        coVerify(exactly = 1) { repository.get(id) }
    }

    @Test
    fun `invoke should return null when repository returns null`() = runTest {
        val id = "999"
        coEvery { repository.get(id) } returns null

        val result = getUseCase(id)

        assertNull(result)
        coVerify(exactly = 1) { repository.get(id) }
    }
}