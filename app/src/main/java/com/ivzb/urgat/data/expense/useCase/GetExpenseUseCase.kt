package com.ivzb.urgat.data.expense.useCase

import com.ivzb.urgat.data.base.BaseGetUseCase
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpensesRepository
import javax.inject.Inject

class GetExpenseUseCase @Inject constructor(
    expensesRepository: ExpensesRepository
): BaseGetUseCase<Expense>(expensesRepository)