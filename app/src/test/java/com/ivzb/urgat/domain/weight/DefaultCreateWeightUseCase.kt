package com.ivzb.urgat.domain.weight

import com.ivzb.urgat.data.weight.WeightsRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultCreateWeightUseCaseTest {

    private lateinit var weightsRepository: WeightsRepository
    private lateinit var createWeightUseCase: CreateWeightUseCase

    @Before
    fun setUp() {
        weightsRepository = mockk(relaxed = true)
        createWeightUseCase = DefaultCreateWeightUseCase(weightsRepository)
    }

    @Test
    fun `invoke should call repository create with correct parameters`() = runTest {
        val value = 79.5.toBigDecimal()

        createWeightUseCase(value)

        coVerify(exactly = 1) {
            weightsRepository.create(value)
        }
    }
}