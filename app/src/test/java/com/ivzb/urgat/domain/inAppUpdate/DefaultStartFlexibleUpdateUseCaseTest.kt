package com.ivzb.urgat.domain.inAppUpdate

import android.app.Activity
import com.ivzb.urgat.data.inAppUpdate.InAppUpdateRepository
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class DefaultStartFlexibleUpdateUseCaseTest {

    private lateinit var repository: InAppUpdateRepository
    private lateinit var useCase: StartFlexibleUpdateUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = DefaultStartFlexibleUpdateUseCase(repository)
    }

    @Test
    fun `invoke calls repository startFlexibleUpdate with activity`() {
        val activity = mockk<Activity>()

        useCase(activity)

        verify(exactly = 1) { repository.startFlexibleUpdate(activity) }
    }
}

