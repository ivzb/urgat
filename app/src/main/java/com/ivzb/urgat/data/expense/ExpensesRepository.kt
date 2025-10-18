package com.ivzb.urgat.data.expense

import kotlinx.coroutines.flow.Flow

interface ExpensesRepository {

    fun getExpensesStream(): Flow<List<Expense>>

}