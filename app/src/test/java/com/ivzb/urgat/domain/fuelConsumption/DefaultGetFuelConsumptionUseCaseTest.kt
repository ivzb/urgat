package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.data.fuelConsumption.FuelConsumption
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class DefaultGetFuelConsumptionUseCaseTest {

    private lateinit var repository: FuelConsumptionRepository
    private lateinit var useCase: GetFuelConsumptionUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = DefaultGetFuelConsumptionUseCase(repository)
    }

    @Test
    fun `invoke should call repository get with correct id`() = runTest {
        val id = "test-id"

        useCase(id)

        coVerify(exactly = 1) { repository.get(id) }
    }

    @Test
    fun `invoke should return fuel consumption when found`() = runTest {
        val id = "test-id"
        val expected = FuelConsumption(
            id = id,
            amount = BigDecimal("45.5"),
            price = BigDecimal("2.35"),
            currency = "EUR",
            type = "Diesel",
            carName = "Mercedes w140",
            odometer = 150000,
            date = 1000L,
            isDeleted = false,
            createdAt = 1000L,
        )
        coEvery { repository.get(id) } returns expected

        val result = useCase(id)

        assertEquals(expected, result)
    }

    @Test
    fun `invoke should return null when not found`() = runTest {
        val id = "non-existent"
        coEvery { repository.get(id) } returns null

        val result = useCase(id)

        assertNull(result)
    }
}

