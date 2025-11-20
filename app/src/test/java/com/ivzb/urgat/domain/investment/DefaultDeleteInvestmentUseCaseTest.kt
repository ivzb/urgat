package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.InvestmentsRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultDeleteInvestmentUseCaseTest {

    private lateinit var repository: InvestmentsRepository
    private lateinit var deleteUseCase: DeleteInvestmentUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true) // relaxed to ignore unmocked calls
        deleteUseCase = DefaultDeleteInvestmentUseCase(repository)
    }

    @Test
    fun `invoke should call repository delete with correct parameters`() = runTest {
        val id = "123"

        deleteUseCase(id)

        coVerify(exactly = 1) {
            repository.delete(id)
        }
    }
}
