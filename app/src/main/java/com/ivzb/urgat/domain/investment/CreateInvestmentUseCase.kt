package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.InvestmentsRepository
import java.math.BigDecimal
import javax.inject.Inject

interface CreateInvestmentUseCase {

    suspend operator fun invoke(
        name: String,
        amount: BigDecimal,
        cost: BigDecimal,
        category: String,
        currency: String,
        date: Long,
    )

}

class DefaultCreateInvestmentUseCase @Inject constructor(
    private val repository: InvestmentsRepository
): CreateInvestmentUseCase {

    override suspend operator fun invoke(
        name: String,
        amount: BigDecimal,
        cost: BigDecimal,
        category: String,
        currency: String,
        date: Long,
    ) {

        repository.create(name, amount, cost, category, currency, date)
    }

}
