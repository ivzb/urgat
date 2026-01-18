package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.data.budget.BudgetsRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class DefaultCreateBudgetUseCaseTest {

    private lateinit var repository: BudgetsRepository
    private lateinit var createUseCase: CreateBudgetUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        createUseCase = DefaultCreateBudgetUseCase(repository)
    }

    @Test
    fun `invoke should call repository create with correct parameters`() = runTest {
        val income = BigDecimal("6500")
        val bankStart = BigDecimal("11000")
        val bankEnd = BigDecimal("8800")
        val date = 1L

        createUseCase(income, bankStart, bankEnd, date)

        coVerify(exactly = 1) {
            repository.create(income, bankStart, bankEnd, date)
        }
    }
}