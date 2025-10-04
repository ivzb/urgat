package com.ivzb.urgat.data.expense

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ExpensesRepository {
    val expenses: Flow<List<String>>

    suspend fun add(name: String)
}

class DefaultExpensesRepository @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpensesRepository {

    override val expenses: Flow<List<String>> =
        expenseDao.getExpenses().map { items -> items.map { it.name } }

    override suspend fun add(name: String) {
        expenseDao.insertExpense(Expense(name = name))
    }
}

class FakeExpensesRepository @Inject constructor() : ExpensesRepository {
    override val expenses: Flow<List<String>> = flowOf(fakeExpensess)

    override suspend fun add(name: String) {
        throw NotImplementedError()
    }
}

val fakeExpensess = listOf("One", "Two", "Three")
