package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.InvestmentsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DefaultGetLastUsedInvestmentCurrencyUseCaseTest {

    private lateinit var repository: InvestmentsRepository
    private lateinit var useCase: GetLastUsedInvestmentCurrencyUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = DefaultGetLastUsedInvestmentCurrencyUseCase(repository)
    }

    @Test
    fun `invoke should call repository getLastUsedCurrency`() = runTest {
        useCase()

        coVerify(exactly = 1) { repository.getLastUsedCurrency() }
    }

    @Test
    fun `invoke should return currency when available`() = runTest {
        coEvery { repository.getLastUsedCurrency() } returns "USD"

        val result = useCase()

        assertEquals("USD", result)
    }

    @Test
    fun `invoke should return null when no currency available`() = runTest {
        coEvery { repository.getLastUsedCurrency() } returns null

        val result = useCase()

        assertNull(result)
    }

}

