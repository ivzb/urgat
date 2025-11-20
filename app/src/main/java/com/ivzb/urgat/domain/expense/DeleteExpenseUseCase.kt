package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.data.expense.ExpensesRepository
import javax.inject.Inject

interface DeleteExpenseUseCase {

    suspend operator fun invoke(id: String)

}

class DefaultDeleteExpenseUseCase @Inject constructor(
    private val repository: ExpensesRepository
): DeleteExpenseUseCase {

    override suspend operator fun invoke(id: String) {
        repository.delete(id)
    }

}