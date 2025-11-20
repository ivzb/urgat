package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.InvestmentsRepository
import javax.inject.Inject

interface DeleteInvestmentUseCase {

    suspend operator fun invoke(id: String)

}

class DefaultDeleteInvestmentUseCase @Inject constructor(
    private val repository: InvestmentsRepository
): DeleteInvestmentUseCase {

    override suspend operator fun invoke(id: String) {
        repository.delete(id)
    }

}