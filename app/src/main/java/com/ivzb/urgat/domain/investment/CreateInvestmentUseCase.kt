package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.InvestmentsRepository
import java.math.BigDecimal
import javax.inject.Inject

interface CreateInvestmentUseCase {

    suspend operator fun invoke(name: String, currency: String, amount: BigDecimal, category: String)

}

class DefaultCreateInvestmentUseCase @Inject constructor(
    private val investmentsRepository: InvestmentsRepository
): CreateInvestmentUseCase {

    override suspend operator fun invoke(name: String, currency: String, amount: BigDecimal, category: String) {
        investmentsRepository.create(name, currency, amount, category)
    }

}
