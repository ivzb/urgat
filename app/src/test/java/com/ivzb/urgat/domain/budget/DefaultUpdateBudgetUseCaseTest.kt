package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.data.budget.BudgetsRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class DefaultUpdateBudgetUseCaseTest {

    private lateinit var repository: BudgetsRepository
    private lateinit var updateUseCase: UpdateBudgetUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true) // relaxed to ignore unmocked calls
        updateUseCase = DefaultUpdateBudgetUseCase(repository)
    }

    @Test
    fun `invoke should call repository update with correct parameters`() = runTest {
        val id = "123"
        val income = BigDecimal("6500")
        val bankStart = BigDecimal("11000")
        val bankEnd = BigDecimal("8800")
        val currency = "EUR"
        val date = 1L

        updateUseCase(id, income, bankStart, bankEnd, currency, date)

        coVerify(exactly = 1) {
            repository.update(id, income, bankStart, bankEnd, currency, date)
        }
    }
}
