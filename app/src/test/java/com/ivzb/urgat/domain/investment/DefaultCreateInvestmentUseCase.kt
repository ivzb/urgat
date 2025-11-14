package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.InvestmentsRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class DefaultCreateInvestmentUseCaseTest {

    private lateinit var investmentsRepository: InvestmentsRepository
    private lateinit var createInvestmentUseCase: CreateInvestmentUseCase

    @Before
    fun setUp() {
        investmentsRepository = mockk(relaxed = true)
        createInvestmentUseCase = DefaultCreateInvestmentUseCase(investmentsRepository)
    }

    @Test
    fun `invoke should call repository create with correct parameters`() = runTest {
        val name = "SNP 500"
        val currency = "USD"
        val amount = BigDecimal("450")
        val category = "etfs"

        createInvestmentUseCase(name, currency, amount, category)

        coVerify(exactly = 1) {
            investmentsRepository.create(name, currency, amount, category)
        }
    }
}