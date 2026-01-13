package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.data.budget.BudgetsRepository
import java.math.BigDecimal
import javax.inject.Inject

interface CreateBudgetUseCase {

    suspend operator fun invoke(
        income: BigDecimal,
        bankStart: BigDecimal,
        bankEnd: BigDecimal?,
        date: Long,
    )

}

class DefaultCreateBudgetUseCase @Inject constructor(
    private val repository: BudgetsRepository
): CreateBudgetUseCase {

    override suspend operator fun invoke(
        income: BigDecimal,
        bankStart: BigDecimal,
        bankEnd: BigDecimal?,
        date: Long,
    ) {

        repository.create(income, bankStart, bankEnd, date)
    }

}
