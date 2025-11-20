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

    private lateinit var repository: InvestmentsRepository
    private lateinit var getUseCase: GetInvestmentUseCase

    @Before
    fun setUp() {
        repository = mockk()
        getUseCase = DefaultGetInvestmentUseCase(repository)
    }

    @Test
    fun `invoke should return investment when repository returns investment`() = runTest {
        val id = "123"
        val expectedInvestment = Investment(id, "SNP 500",  "USD", BigDecimal("350"), "etfs")
        coEvery { repository.get(id) } returns expectedInvestment

        val result = getUseCase(id)

        assertEquals(expectedInvestment, result)
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