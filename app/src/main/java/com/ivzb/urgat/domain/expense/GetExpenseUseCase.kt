package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.domain.base.BaseGetUseCase
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpensesRepository
import javax.inject.Inject

interface GetExpenseUseCase {

    suspend operator fun invoke(id: String): Expense?

}

class DefaultGetExpenseUseCase @Inject constructor(
    expensesRepository: ExpensesRepository
): GetExpenseUseCase, BaseGetUseCase<Expense>(expensesRepository)