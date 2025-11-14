package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.InvestmentsRepository
import java.math.BigDecimal
import javax.inject.Inject

interface UpdateInvestmentUseCase {

    suspend operator fun invoke(id: String, name: String, currency: String, amount: BigDecimal, category: String)

}

class DefaultUpdateInvestmentUseCase @Inject constructor(
    private val investmentsRepository: InvestmentsRepository
): UpdateInvestmentUseCase {

    override suspend operator fun invoke(id: String, name: String, currency: String, amount: BigDecimal, category: String) {
        investmentsRepository.update(id, name, currency, amount, category)
    }

}
