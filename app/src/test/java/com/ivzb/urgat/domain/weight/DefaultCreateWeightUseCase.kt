package com.ivzb.urgat.domain.weight

import com.ivzb.urgat.data.weight.WeightsRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultCreateWeightUseCaseTest {

    private lateinit var repository: WeightsRepository
    private lateinit var createUseCase: CreateWeightUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        createUseCase = DefaultCreateWeightUseCase(repository)
    }

    @Test
    fun `invoke should call repository create with correct parameters`() = runTest {
        val value = 79.5.toBigDecimal()
        val date = 1764523811L

        createUseCase(value, date)

        coVerify(exactly = 1) {
            repository.create(value, date)
        }
    }
}