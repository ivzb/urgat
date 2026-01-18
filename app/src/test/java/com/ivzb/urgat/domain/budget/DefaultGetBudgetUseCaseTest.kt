package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.budget.BudgetsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DefaultGetBudgetUseCaseTest {

    private lateinit var repository: BudgetsRepository
    private lateinit var getUseCase: GetBudgetUseCase

    @Before
    fun setUp() {
        repository = mockk()
        getUseCase = DefaultGetBudgetUseCase(repository)
    }

    @Test
    fun `invoke should return budget when repository returns investment`() = runTest {
        val id = "123"
        val expectedBudget = Budget(
            id = id,
            income = 6500.toBigDecimal(),
            bankStart = 11000.toBigDecimal(),
            bankEnd = 8400.toBigDecimal(),
            date = 123L,
            isDeleted = false,
            createdAt = 1L,
        )

        coEvery { repository.get(id) } returns expectedBudget

        val result = getUseCase(id)

        assertEquals(expectedBudget, result)
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