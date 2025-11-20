package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.data.expense.ExpensesRepository
import java.math.BigDecimal
import javax.inject.Inject

interface UpdateExpenseUseCase {

    suspend operator fun invoke(id: String, name: String, amount: BigDecimal, category: String, date: Long)

}

class DefaultUpdateExpenseUseCase @Inject constructor(
    private val repository: ExpensesRepository
): UpdateExpenseUseCase {

    override suspend operator fun invoke(id: String, name: String, amount: BigDecimal, category: String, date: Long) {
        repository.update(id, name, amount, category, date)
    }

}
