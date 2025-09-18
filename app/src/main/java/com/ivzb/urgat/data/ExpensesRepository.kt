package com.ivzb.urgat.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.ivzb.urgat.data.local.database.Expenses
import com.ivzb.urgat.data.local.database.ExpensesDao
import javax.inject.Inject

interface ExpensesRepository {
    val expensess: Flow<List<String>>

    suspend fun add(name: String)
}

class DefaultExpensesRepository @Inject constructor(
    private val expensesDao: ExpensesDao
) : ExpensesRepository {

    override val expensess: Flow<List<String>> =
        expensesDao.getExpensess().map { items -> items.map { it.name } }

    override suspend fun add(name: String) {
        expensesDao.insertExpenses(Expenses(name = name))
    }
}
