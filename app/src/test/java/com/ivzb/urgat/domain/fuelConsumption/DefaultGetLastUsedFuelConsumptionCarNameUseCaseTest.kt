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

class DefaultGetLastUsedFuelConsumptionCarNameUseCaseTest {

    private lateinit var repository: FuelConsumptionRepository
    private lateinit var useCase: GetLastUsedFuelConsumptionCarNameUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = DefaultGetLastUsedFuelConsumptionCarNameUseCase(repository)
    }

    @Test
    fun `invoke should call repository getLastUsedCarName`() = runTest {
        useCase()

        coVerify(exactly = 1) { repository.getLastUsedCarName() }
    }

    @Test
    fun `invoke should return car name when available`() = runTest {
        coEvery { repository.getLastUsedCarName() } returns "Mercedes w140"

        val result = useCase()

        assertEquals("Mercedes w140", result)
    }

    @Test
    fun `invoke should return null when no car name available`() = runTest {
        coEvery { repository.getLastUsedCarName() } returns null

        val result = useCase()

        assertNull(result)
    }
}

