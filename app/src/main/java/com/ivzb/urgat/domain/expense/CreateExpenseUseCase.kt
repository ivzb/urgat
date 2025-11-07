package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.data.expense.ExpensesRepository
import java.math.BigDecimal
import javax.inject.Inject

interface CreateExpenseUseCase {

    suspend operator fun invoke(name: String, amount: BigDecimal, category: String, date: Long)

}

class DefaultCreateExpenseUseCase @Inject constructor(
    private val expensesRepository: ExpensesRepository
): CreateExpenseUseCase {

    override suspend operator fun invoke(name: String, amount: BigDecimal, category: String, date: Long) {
        expensesRepository.create(name, amount, category, date)
    }

}
