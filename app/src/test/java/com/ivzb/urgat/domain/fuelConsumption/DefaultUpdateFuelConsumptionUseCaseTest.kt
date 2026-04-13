package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class DefaultUpdateFuelConsumptionUseCaseTest {

    private lateinit var repository: FuelConsumptionRepository
    private lateinit var useCase: UpdateFuelConsumptionUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = DefaultUpdateFuelConsumptionUseCase(repository)
    }

    @Test
    fun `invoke should call repository update with correct parameters`() = runTest {
        val id = "test-id"
        val amount = BigDecimal("45.5")
        val price = BigDecimal("2.35")
        val currency = "EUR"
        val type = "Diesel"
        val carName = "Mercedes w140"
        val odometer = 150000
        val date = 1000L

        useCase(id, amount, price, currency, type, carName, odometer, date)

        coVerify(exactly = 1) {
            repository.update(id, amount, price, currency, type, carName, odometer, date)
        }
    }

    @Test
    fun `invoke should call repository update with different parameters`() = runTest {
        val id = "other-id"
        val amount = BigDecimal("60.0")
        val price = BigDecimal("1.95")
        val currency = "USD"
        val type = "Gasoline"
        val carName = "BMW E39"
        val odometer = 200000
        val date = 2000L

        useCase(id, amount, price, currency, type, carName, odometer, date)

        coVerify(exactly = 1) {
            repository.update(id, amount, price, currency, type, carName, odometer, date)
        }
    }
}

