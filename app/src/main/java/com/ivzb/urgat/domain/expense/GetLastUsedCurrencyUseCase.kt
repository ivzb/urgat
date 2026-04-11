package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.data.expense.ExpensesRepository
import javax.inject.Inject

interface GetLastUsedCurrencyUseCase {

    suspend operator fun invoke(): String?

}

class DefaultGetLastUsedCurrencyUseCase @Inject constructor(
    private val repository: ExpensesRepository
): GetLastUsedCurrencyUseCase {

    override suspend fun invoke(): String? {
        return repository.getLastUsedCurrency()
    }

}

