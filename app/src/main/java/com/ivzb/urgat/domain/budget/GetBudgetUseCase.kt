package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.budget.BudgetsRepository
import javax.inject.Inject

interface GetBudgetUseCase {

    suspend operator fun invoke(id: String): Budget?

}

class DefaultGetBudgetUseCase @Inject constructor(
    private val repository: BudgetsRepository
): GetBudgetUseCase {

    override suspend fun invoke(id: String): Budget? {
        return repository.get(id)
    }

}