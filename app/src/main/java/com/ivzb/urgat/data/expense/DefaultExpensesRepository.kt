package com.ivzb.urgat.data.expense

import com.ivzb.urgat.di.ApplicationScope
import com.ivzb.urgat.di.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultExpensesRepository @Inject constructor(
    private val localDataSource: ExpenseDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) : ExpensesRepository {

    override fun getExpensesStream(): Flow<List<Expense>> {
        return localDataSource.observeAll().map { expenses ->
            withContext(dispatcher) {
                expenses.toExternal()
            }
        }
    }

}
