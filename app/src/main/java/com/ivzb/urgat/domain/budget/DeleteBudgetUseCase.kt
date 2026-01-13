package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.data.budget.BudgetsRepository
import javax.inject.Inject

interface DeleteBudgetUseCase {

    suspend operator fun invoke(id: String)

}

class DefaultDeleteBudgetUseCase @Inject constructor(
    private val repository: BudgetsRepository
): DeleteBudgetUseCase {

    override suspend operator fun invoke(id: String) {
        repository.delete(id)
    }

}