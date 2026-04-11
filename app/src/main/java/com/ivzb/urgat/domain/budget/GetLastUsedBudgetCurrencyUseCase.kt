package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.data.budget.BudgetsRepository
import javax.inject.Inject

interface GetLastUsedBudgetCurrencyUseCase {

    suspend operator fun invoke(): String?

}

class DefaultGetLastUsedBudgetCurrencyUseCase @Inject constructor(
    private val repository: BudgetsRepository
): GetLastUsedBudgetCurrencyUseCase {

    override suspend fun invoke(): String? {
        return repository.getLastUsedCurrency()
    }

}

