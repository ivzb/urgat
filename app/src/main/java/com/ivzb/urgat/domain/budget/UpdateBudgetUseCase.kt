package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.data.budget.BudgetsRepository
import java.math.BigDecimal
import javax.inject.Inject

interface UpdateBudgetUseCase {

    suspend operator fun invoke(
        id: String,
        income: BigDecimal,
        bankStart: BigDecimal,
        bankEnd: BigDecimal?,
        currency: String,
        date: Long,
    )

}

class DefaultUpdateBudgetUseCase @Inject constructor(
    private val repository: BudgetsRepository
): UpdateBudgetUseCase {

    override suspend operator fun invoke(
        id: String,
        income: BigDecimal,
        bankStart: BigDecimal,
        bankEnd: BigDecimal?,
        currency: String,
        date: Long,
    ) {

        repository.update(id, income, bankStart, bankEnd, currency, date)
    }

}
