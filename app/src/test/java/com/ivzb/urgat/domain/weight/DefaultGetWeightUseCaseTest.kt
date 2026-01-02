package com.ivzb.urgat.domain.weight

import com.ivzb.urgat.data.weight.Weight
import com.ivzb.urgat.data.weight.WeightsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DefaultGetWeightUseCaseTest {

    private lateinit var repository: WeightsRepository
    private lateinit var getUseCase: GetWeightUseCase

    @Before
    fun setUp() {
        repository = mockk()
        getUseCase = DefaultGetWeightUseCase(repository)
    }

    @Test
    fun `invoke should return weight when repository returns weight`() = runTest {
        val id = "123"
        val expectedWeight = Weight(id, "80.5".toBigDecimal(),  1L, false, 1L)
        coEvery { repository.get(id) } returns expectedWeight

        val result = getUseCase(id)

        assertEquals(expectedWeight, result)
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