package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.InvestmentsRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultDeleteInvestmentUseCaseTest {

    private lateinit var investmentsRepository: InvestmentsRepository
    private lateinit var deleteInvestmentUseCase: DeleteInvestmentUseCase

    @Before
    fun setUp() {
        investmentsRepository = mockk(relaxed = true) // relaxed to ignore unmocked calls
        deleteInvestmentUseCase = DefaultDeleteInvestmentUseCase(investmentsRepository)
    }

    @Test
    fun `invoke should call repository delete with correct parameters`() = runTest {
        val id = "123"

        deleteInvestmentUseCase(id)

        coVerify(exactly = 1) {
            investmentsRepository.delete(id)
        }
    }
}
