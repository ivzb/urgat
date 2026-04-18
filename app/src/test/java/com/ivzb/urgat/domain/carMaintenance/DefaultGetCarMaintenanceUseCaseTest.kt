package com.ivzb.urgat.domain.carMaintenance

import com.ivzb.urgat.data.carMaintenance.CarMaintenance
import com.ivzb.urgat.data.carMaintenance.CarMaintenanceRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class DefaultGetCarMaintenanceUseCaseTest {

    private lateinit var repository: CarMaintenanceRepository
    private lateinit var useCase: GetCarMaintenanceUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = DefaultGetCarMaintenanceUseCase(repository)
    }

    @Test
    fun `invoke should call repository get with correct id`() = runTest {
        val id = "test-id"

        useCase(id)

        coVerify(exactly = 1) { repository.get(id) }
    }

    @Test
    fun `invoke should return car maintenance when found`() = runTest {
        val id = "test-id"
        val expected = CarMaintenance(
            id = id,
            description = "Oil change",
            partNumber = "ABC123",
            url = "https://example.com",
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

