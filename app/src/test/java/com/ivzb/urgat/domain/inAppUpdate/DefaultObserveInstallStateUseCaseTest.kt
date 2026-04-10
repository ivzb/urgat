package com.ivzb.urgat.domain.inAppUpdate

import com.ivzb.urgat.data.inAppUpdate.InAppInstallState
import com.ivzb.urgat.data.inAppUpdate.InAppUpdateRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DefaultObserveInstallStateUseCaseTest {

    private lateinit var repository: InAppUpdateRepository
    private lateinit var useCase: ObserveInstallStateUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = DefaultObserveInstallStateUseCase(repository)
    }

    @Test
    fun `invoke returns flow from repository`() = runTest {
        val expected = InAppInstallState.Downloaded
        every { repository.observeInstallState() } returns flowOf(expected)

        val result = useCase().first()

        assertEquals(expected, result)
        verify(exactly = 1) { repository.observeInstallState() }
    }

    @Test
    fun `invoke returns downloading state from repository`() = runTest {
        val expected = InAppInstallState.Downloading(bytesDownloaded = 50, totalBytesToDownload = 100)
        every { repository.observeInstallState() } returns flowOf(expected)

        val result = useCase().first()

        assertEquals(expected, result)
    }
}

