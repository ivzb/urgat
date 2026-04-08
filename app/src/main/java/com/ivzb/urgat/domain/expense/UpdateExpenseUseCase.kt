package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.data.expense.ExpensesRepository
import java.math.BigDecimal
import javax.inject.Inject

interface UpdateExpenseUseCase {

    suspend operator fun invoke(
        id: String,
        name: String,
        amount: BigDecimal,
        currency: String,
        category: String,
        date: Long,
        excludeFromAnalytics: Boolean,
    )

}

class DefaultUpdateExpenseUseCase @Inject constructor(
    private val repository: ExpensesRepository
): UpdateExpenseUseCase {

    override suspend operator fun invoke(
        id: String,
        name: String,
        amount: BigDecimal,
        currency: String,
        category: String,
        date: Long,
        excludeFromAnalytics: Boolean
    ) {

        repository.update(id, name, amount, currency, category, date, excludeFromAnalytics)
    }

}
