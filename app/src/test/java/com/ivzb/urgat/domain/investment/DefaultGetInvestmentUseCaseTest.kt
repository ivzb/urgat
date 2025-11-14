package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DefaultGetInvestmentUseCaseTest {

    private lateinit var investmentsRepository: InvestmentsRepository
    private lateinit var getInvestmentUseCase: GetInvestmentUseCase

    @Before
    fun setUp() {
        investmentsRepository = mockk()
        getInvestmentUseCase = DefaultGetInvestmentUseCase(investmentsRepository)
    }

    @Test
    fun `invoke should return investment when repository returns investment`() = runTest {
        val id = "123"
        val expectedInvestment = Investment(id, "SNP 500",  "USD", BigDecimal("350"), "etfs")
        coEvery { investmentsRepository.get(id) } returns expectedInvestment

        val result = getInvestmentUseCase(id)

        assertEquals(expectedInvestment, result)
        coVerify(exactly = 1) { investmentsRepository.get(id) }
    }

    @Test
    fun `invoke should return null when repository returns null`() = runTest {
        val id = "999"
        coEvery { investmentsRepository.get(id) } returns null

        val result = getInvestmentUseCase(id)

        assertNull(result)
        coVerify(exactly = 1) { investmentsRepository.get(id) }
    }
}