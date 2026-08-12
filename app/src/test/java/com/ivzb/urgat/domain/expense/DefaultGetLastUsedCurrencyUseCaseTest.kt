package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.data.expense.ExpensesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DefaultGetLastUsedCurrencyUseCaseTest {

    private lateinit var repository: ExpensesRepository
    private lateinit var useCase: GetLastUsedCurrencyUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = DefaultGetLastUsedCurrencyUseCase(repository)
    }

    @Test
    fun `invoke should call repository getLastUsedCurrency`() = runTest {
        useCase()

        coVerify(exactly = 1) { repository.getLastUsedCurrency() }
    }

    @Test
    fun `invoke should return currency when available`() = runTest {
        coEvery { repository.getLastUsedCurrency() } returns "EUR"

        val result = useCase()

        assertEquals("EUR", result)
    }

    @Test
    fun `invoke should return null when no currency available`() = runTest {
        coEvery { repository.getLastUsedCurrency() } returns null

        val result = useCase()

        assertNull(result)
    }

}

