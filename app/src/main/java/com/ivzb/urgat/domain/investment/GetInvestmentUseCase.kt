package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentsRepository
import javax.inject.Inject

interface GetInvestmentUseCase {

    suspend operator fun invoke(id: String): Investment?

}

class DefaultGetInvestmentUseCase @Inject constructor(
    private val repository: InvestmentsRepository
): GetInvestmentUseCase {

    override suspend fun invoke(id: String): Investment? {
        return repository.get(id)
    }

}