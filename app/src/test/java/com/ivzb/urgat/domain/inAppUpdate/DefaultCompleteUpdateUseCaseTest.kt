package com.ivzb.urgat.domain.inAppUpdate

import com.ivzb.urgat.data.inAppUpdate.InAppUpdateRepository
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class DefaultCompleteUpdateUseCaseTest {

    private lateinit var repository: InAppUpdateRepository
    private lateinit var useCase: CompleteUpdateUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = DefaultCompleteUpdateUseCase(repository)
    }

    @Test
    fun `invoke calls repository completeUpdate`() {
        useCase()

        verify(exactly = 1) { repository.completeUpdate() }
    }
}

