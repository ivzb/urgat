package com.ivzb.urgat.domain.saving

import com.ivzb.urgat.data.saving.SavingsRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class DefaultUpdateSavingUseCaseTest {

    private lateinit var repository: SavingsRepository
    private lateinit var updateUseCase: UpdateSavingUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true) // relaxed to ignore unmocked calls
        updateUseCase = DefaultUpdateSavingUseCase(repository)
    }

    @Test
    fun `invoke should call repository update with correct parameters`() = runTest {
        val id = "123"
        val name = "Emergency fund"
        val amount = BigDecimal("500")
        val date = 1731268800000L

        updateUseCase(id, name, amount, date)

        coVerify(exactly = 1) {
            repository.update(id, name, amount, date)
        }
    }
}
