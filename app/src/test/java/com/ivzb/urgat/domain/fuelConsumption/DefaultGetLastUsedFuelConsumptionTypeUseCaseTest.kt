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

class DefaultGetLastUsedFuelConsumptionTypeUseCaseTest {

    private lateinit var repository: FuelConsumptionRepository
    private lateinit var useCase: GetLastUsedFuelConsumptionTypeUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = DefaultGetLastUsedFuelConsumptionTypeUseCase(repository)
    }

    @Test
    fun `invoke should call repository getLastUsedType`() = runTest {
        useCase()

        coVerify(exactly = 1) { repository.getLastUsedType() }
    }

    @Test
    fun `invoke should return type when available`() = runTest {
        coEvery { repository.getLastUsedType() } returns "Diesel"

        val result = useCase()

        assertEquals("Diesel", result)
    }

    @Test
    fun `invoke should return null when no type available`() = runTest {
        coEvery { repository.getLastUsedType() } returns null

        val result = useCase()

        assertNull(result)
    }
}

