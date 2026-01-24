package com.ivzb.urgat.domain.saving

import com.ivzb.urgat.data.saving.SavingsRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class DefaultCreateSavingUseCaseTest {

    private lateinit var repository: SavingsRepository
    private lateinit var createUseCase: CreateSavingUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        createUseCase = DefaultCreateSavingUseCase(repository)
    }

    @Test
    fun `invoke should call repository create with correct parameters`() = runTest {
        val name = "Emergency fund"
        val amount = BigDecimal("500")
        val date = 1731268800000L

        createUseCase(name, amount, date)

        coVerify(exactly = 1) {
            repository.create(name, amount, date)
        }
    }
}