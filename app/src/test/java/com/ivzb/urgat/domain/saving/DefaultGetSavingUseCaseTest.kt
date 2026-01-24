package com.ivzb.urgat.domain.saving

import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.saving.SavingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DefaultGetSavingUseCaseTest {

    private lateinit var repository: SavingsRepository
    private lateinit var getUseCase: GetSavingUseCase

    @Before
    fun setUp() {
        repository = mockk()
        getUseCase = DefaultGetSavingUseCase(repository)
    }

    @Test
    fun `invoke should return saving when repository returns saving`() = runTest {
        val id = "123"
        val expectedSaving = Saving(id, "Emergency fund", BigDecimal("500"), 1731268800000L, false, 1731268800000L)
        coEvery { repository.get(id) } returns expectedSaving

        val result = getUseCase(id)

        assertEquals(expectedSaving, result)
        coVerify(exactly = 1) { repository.get(id) }
    }

    @Test
    fun `invoke should return null when repository returns null`() = runTest {
        val id = "999"
        coEvery { repository.get(id) } returns null

        val result = getUseCase(id)

        assertNull(result)
        coVerify(exactly = 1) { repository.get(id) }
    }
}