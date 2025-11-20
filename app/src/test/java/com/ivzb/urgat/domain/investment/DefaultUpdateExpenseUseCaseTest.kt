package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.InvestmentsRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class DefaultUpdateInvestmentUseCaseTest {

    private lateinit var repository: InvestmentsRepository
    private lateinit var updateUseCase: UpdateInvestmentUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true) // relaxed to ignore unmocked calls
        updateUseCase = DefaultUpdateInvestmentUseCase(repository)
    }

    @Test
    fun `invoke should call repository update with correct parameters`() = runTest {
        val id = "123"
        val name = "SNP 500"
        val currency = "USD"
        val amount = BigDecimal("1550")
        val category = "etfs"

        updateUseCase(id, name, currency, amount, category)

        coVerify(exactly = 1) {
            repository.update(id, name, currency, amount, category)
        }
    }
}
