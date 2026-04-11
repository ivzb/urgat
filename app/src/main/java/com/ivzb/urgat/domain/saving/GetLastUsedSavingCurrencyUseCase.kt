package com.ivzb.urgat.domain.saving

import com.ivzb.urgat.data.saving.SavingsRepository
import javax.inject.Inject

interface GetLastUsedSavingCurrencyUseCase {

    suspend operator fun invoke(): String?

}

class DefaultGetLastUsedSavingCurrencyUseCase @Inject constructor(
    private val repository: SavingsRepository
): GetLastUsedSavingCurrencyUseCase {

    override suspend fun invoke(): String? {
        return repository.getLastUsedCurrency()
    }

}

