package com.ivzb.urgat.domain.carMaintenance

import com.ivzb.urgat.data.carMaintenance.CarMaintenanceRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultCreateCarMaintenanceUseCaseTest {

    private lateinit var repository: CarMaintenanceRepository
    private lateinit var useCase: CreateCarMaintenanceUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = DefaultCreateCarMaintenanceUseCase(repository)
    }

    @Test
    fun `invoke should call repository create with correct parameters`() = runTest {
        val description = "Oil change"
        val partNumber = "ABC123"
        val url = "https://example.com"
        val carName = "Mercedes w140"
        val odometer = 150000
        val date = 1000L

        useCase(description, partNumber, url, carName, odometer, date)

        coVerify(exactly = 1) {
            repository.create(description, partNumber, url, carName, odometer, date)
        }
    }

    @Test
    fun `invoke should call repository create with null url`() = runTest {
        useCase("Brake pads", "XYZ789", null, "BMW E39", 200000, 2000L)

        coVerify(exactly = 1) {
            repository.create("Brake pads", "XYZ789", null, "BMW E39", 200000, 2000L)
        }
    }
}

