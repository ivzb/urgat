package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultDeleteFuelConsumptionUseCaseTest {

    private lateinit var repository: FuelConsumptionRepository
    private lateinit var useCase: DeleteFuelConsumptionUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = DefaultDeleteFuelConsumptionUseCase(repository)
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

