package com.ivzb.urgat.domain.weight

import com.ivzb.urgat.data.weight.WeightsRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class DefaultUpdateWeightUseCaseTest {

    private lateinit var repository: WeightsRepository
    private lateinit var updateUseCase: UpdateWeightUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true) // relaxed to ignore unmocked calls
        updateUseCase = DefaultUpdateWeightUseCase(repository)
    }

    @Test
    fun `invoke should call repository update with correct parameters`() = runTest {
        val id = "123"
        val value = BigDecimal("80.5")
        val date = 1L

        updateUseCase(id, value, date)

        coVerify(exactly = 1) {
            repository.update(id, value, date)
        }
    }
}
