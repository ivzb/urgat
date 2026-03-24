package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.InvestmentsRepository
import java.math.BigDecimal
import javax.inject.Inject

interface CreateInvestmentUseCase {

    suspend operator fun invoke(
        name: String,
        shares: BigDecimal,
        costPrice: BigDecimal,
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
        shares: BigDecimal,
        costPrice: BigDecimal,
        category: String,
        currency: String,
        date: Long,
    ) {

        repository.create(name, shares, costPrice, category, currency, date)
    }

}
