package com.ivzb.urgat.domain.inAppUpdate

import com.ivzb.urgat.data.inAppUpdate.InAppUpdateRepository
import com.ivzb.urgat.data.inAppUpdate.InAppUpdateStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DefaultCheckForUpdateUseCaseTest {

    private lateinit var repository: InAppUpdateRepository
    private lateinit var useCase: CheckForUpdateUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = DefaultCheckForUpdateUseCase(repository)
    }

    @Test
    fun `invoke returns UpdateAvailable from repository`() = runTest {
        coEvery { repository.checkForUpdate() } returns InAppUpdateStatus.UpdateAvailable

        val result = useCase()

        assertEquals(InAppUpdateStatus.UpdateAvailable, result)
        coVerify(exactly = 1) { repository.checkForUpdate() }
    }

    @Test
    fun `invoke returns NoUpdate from repository`() = runTest {
        coEvery { repository.checkForUpdate() } returns InAppUpdateStatus.NoUpdate

        val result = useCase()

        assertEquals(InAppUpdateStatus.NoUpdate, result)
    }

    @Test
    fun `invoke returns Downloaded from repository`() = runTest {
        coEvery { repository.checkForUpdate() } returns InAppUpdateStatus.Downloaded

        val result = useCase()

        assertEquals(InAppUpdateStatus.Downloaded, result)
    }

    @Test
    fun `invoke returns InProgress from repository`() = runTest {
        coEvery { repository.checkForUpdate() } returns InAppUpdateStatus.InProgress

        val result = useCase()

        assertEquals(InAppUpdateStatus.InProgress, result)
    }
}

