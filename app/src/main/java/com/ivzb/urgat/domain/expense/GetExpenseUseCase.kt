package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpensesRepository
import javax.inject.Inject

interface GetExpenseUseCase {

    suspend operator fun invoke(id: String): Expense?

}

class DefaultGetExpenseUseCase @Inject constructor(
    private val repository: ExpensesRepository
): GetExpenseUseCase {

    override suspend fun invoke(id: String): Expense? {
        return repository.get(id)
    }

}