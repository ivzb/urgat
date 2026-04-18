package com.ivzb.urgat.domain.carMaintenance

import com.ivzb.urgat.data.carMaintenance.CarMaintenanceRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultUpdateCarMaintenanceUseCaseTest {

    private lateinit var repository: CarMaintenanceRepository
    private lateinit var useCase: UpdateCarMaintenanceUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = DefaultUpdateCarMaintenanceUseCase(repository)
    }

    @Test
    fun `invoke should call repository update with correct parameters`() = runTest {
        val id = "test-id"
        val description = "Oil change"
        val partNumber = "ABC123"
        val url = "https://example.com"
        val carName = "Mercedes w140"
        val odometer = 150000
        val date = 1000L

        useCase(id, description, partNumber, url, carName, odometer, date)

        coVerify(exactly = 1) {
            repository.update(id, description, partNumber, url, carName, odometer, date)
        }
    }

    @Test
    fun `invoke should call repository update with null url`() = runTest {
        useCase("id", "Brake pads", "XYZ789", null, "BMW E39", 200000, 2000L)

        coVerify(exactly = 1) {
            repository.update("id", "Brake pads", "XYZ789", null, "BMW E39", 200000, 2000L)
        }
    }
}

