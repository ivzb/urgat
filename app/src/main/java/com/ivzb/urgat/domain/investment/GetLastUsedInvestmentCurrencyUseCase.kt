package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.InvestmentsRepository
import javax.inject.Inject

interface GetLastUsedInvestmentCurrencyUseCase {

    suspend operator fun invoke(): String?

}

class DefaultGetLastUsedInvestmentCurrencyUseCase @Inject constructor(
    private val repository: InvestmentsRepository
): GetLastUsedInvestmentCurrencyUseCase {

    override suspend fun invoke(): String? {
        return repository.getLastUsedCurrency()
    }

}

