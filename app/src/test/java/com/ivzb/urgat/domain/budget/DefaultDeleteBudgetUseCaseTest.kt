package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.data.budget.BudgetsRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultDeleteBudgetUseCaseTest {

    private lateinit var repository: BudgetsRepository
    private lateinit var deleteUseCase: DeleteBudgetUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true) // relaxed to ignore unmocked calls
        deleteUseCase = DefaultDeleteBudgetUseCase(repository)
    }

    @Test
    fun `invoke should call repository delete with correct parameters`() = runTest {
        val id = "123"

        deleteUseCase(id)

        coVerify(exactly = 1) {
            repository.delete(id)
        }
    }
}
