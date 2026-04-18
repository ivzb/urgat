package com.ivzb.urgat.domain.carMaintenance

import com.ivzb.urgat.data.carMaintenance.CarMaintenanceRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultDeleteCarMaintenanceUseCaseTest {

    private lateinit var repository: CarMaintenanceRepository
    private lateinit var useCase: DeleteCarMaintenanceUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = DefaultDeleteCarMaintenanceUseCase(repository)
    }

    @Test
    fun `invoke should call repository delete with correct id`() = runTest {
        val id = "test-id"

        useCase(id)

        coVerify(exactly = 1) { repository.delete(id) }
    }

    @Test
    fun `invoke should call repository delete with different id`() = runTest {
        val id = "other-id"

        useCase(id)

        coVerify(exactly = 1) { repository.delete(id) }
    }
}

