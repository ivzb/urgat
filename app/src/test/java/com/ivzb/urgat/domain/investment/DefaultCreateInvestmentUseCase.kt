package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.InvestmentsRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class DefaultCreateInvestmentUseCaseTest {

    private lateinit var repository: InvestmentsRepository
    private lateinit var createUseCase: CreateInvestmentUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        createUseCase = DefaultCreateInvestmentUseCase(repository)
    }

    @Test
    fun `invoke should call repository create with correct parameters`() = runTest {
        val name = "SNP 500"
        val amount = BigDecimal("450")
        val cost = BigDecimal("250")
        val category = "etfs"
        val currency = "USD"
        val date = 1L

        createUseCase(name, amount, cost, category, currency, date)

        coVerify(exactly = 1) {
            repository.create(name, amount, cost, category, currency, date)
        }
    }
}