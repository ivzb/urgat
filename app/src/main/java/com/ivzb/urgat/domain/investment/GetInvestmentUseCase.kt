package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.domain.base.BaseGetUseCase
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentsRepository
import javax.inject.Inject

interface GetInvestmentUseCase {

    suspend operator fun invoke(id: String): Investment?

}

class DefaultGetInvestmentUseCase @Inject constructor(
    repository: InvestmentsRepository
): GetInvestmentUseCase, BaseGetUseCase<Investment>(repository)