package com.ivzb.urgat.data.expense.useCase
import com.ivzb.urgat.data.expense.ExpensesRepository
import java.math.BigDecimal
import javax.inject.Inject

class CreateExpenseUseCase @Inject constructor(
    private val expensesRepository: ExpensesRepository
) {

    suspend operator fun invoke(name: String, amount: BigDecimal, category: String, date: Long) {
        expensesRepository.create(name, amount, category, date)
    }

}
