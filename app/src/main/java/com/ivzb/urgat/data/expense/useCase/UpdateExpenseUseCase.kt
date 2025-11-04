package com.ivzb.urgat.data.expense.useCase

import com.ivzb.urgat.data.expense.ExpensesRepository
import java.math.BigDecimal
import javax.inject.Inject

interface UpdateExpenseUseCase {

    suspend operator fun invoke(id: String, name: String, amount: BigDecimal, category: String, date: Long)

}

class DefaultUpdateExpenseUseCase @Inject constructor(
    private val expensesRepository: ExpensesRepository
): UpdateExpenseUseCase {

    override suspend operator fun invoke(id: String, name: String, amount: BigDecimal, category: String, date: Long) {
        expensesRepository.update(id, name, amount, category, date)
    }

}
