package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class DefaultGetLastUsedFuelConsumptionCurrencyUseCaseTest {

    private lateinit var repository: FuelConsumptionRepository
    private lateinit var useCase: GetLastUsedFuelConsumptionCurrencyUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = DefaultGetLastUsedFuelConsumptionCurrencyUseCase(repository)
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

