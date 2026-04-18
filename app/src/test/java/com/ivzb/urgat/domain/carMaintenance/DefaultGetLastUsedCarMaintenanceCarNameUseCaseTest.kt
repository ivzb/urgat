package com.ivzb.urgat.domain.carMaintenance

import com.ivzb.urgat.data.carMaintenance.CarMaintenanceRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class DefaultGetLastUsedCarMaintenanceCarNameUseCaseTest {

    private lateinit var repository: CarMaintenanceRepository
    private lateinit var useCase: GetLastUsedCarMaintenanceCarNameUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = DefaultGetLastUsedCarMaintenanceCarNameUseCase(repository)
    }

    @Test
    fun `invoke returns car name from repository`() = runTest {
        coEvery { repository.getLastUsedCarName() } returns "Mercedes w140"

        val result = useCase()

        assertEquals("Mercedes w140", result)
    }

    @Test
    fun `invoke returns null when no car maintenance exists`() = runTest {
        coEvery { repository.getLastUsedCarName() } returns null

        val result = useCase()

        assertNull(result)
    }
}

