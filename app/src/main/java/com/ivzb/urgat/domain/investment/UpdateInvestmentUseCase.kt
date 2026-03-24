package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.InvestmentsRepository
import java.math.BigDecimal
import javax.inject.Inject

interface UpdateInvestmentUseCase {

    suspend operator fun invoke(
        id: String,
        name: String,
        shares: BigDecimal,
        costPrice: BigDecimal,
        category: String,
        currency: String,
        date: Long,
    )

}

class DefaultUpdateInvestmentUseCase @Inject constructor(
    private val repository: InvestmentsRepository
): UpdateInvestmentUseCase {

    override suspend operator fun invoke(
        id: String,
        name: String,
        shares: BigDecimal,
        costPrice: BigDecimal,
        category: String,
        currency: String,
        date: Long,
    ) {

        repository.update(id, name, shares, costPrice, category, currency, date)
    }

}
