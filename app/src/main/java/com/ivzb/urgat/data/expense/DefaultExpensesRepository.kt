package com.ivzb.urgat.data.expense

import com.ivzb.urgat.di.ApplicationScope
import com.ivzb.urgat.di.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

class DefaultExpensesRepository @Inject constructor(
    private val localDataSource: ExpenseDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) : ExpensesRepository {

    override fun getStream(): Flow<List<Expense>> {
        return localDataSource.observeAll().map { expenses ->
            withContext(dispatcher) {
                expenses.toExternal()
            }
        }
    }

    override fun getStream(searchQuery: String): Flow<List<Expense>> {
        return localDataSource.observeAll(searchQuery).map { expenses ->
            withContext(dispatcher) {
                expenses.toExternal()
            }
        }
    }

    override fun getStream(limit: Int): Flow<List<Expense>> {
        return localDataSource.observeAll(limit).map { expenses ->
            withContext(dispatcher) {
                expenses.toExternal()
            }
        }
    }

    override fun getStream(limit: Int, searchQuery: String): Flow<List<Expense>> {
        return localDataSource.observeAll(limit, searchQuery).map { expenses ->
            withContext(dispatcher) {
                expenses.toExternal()
            }
        }
    }

    override suspend fun create(name: String, amount: BigDecimal, category: String, date: Long): String {
        // ID creation might be a complex operation so it's executed using the supplied
        // coroutine dispatcher
        val id = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val expense = Expense(
            id = id,
            name = name,
            amount = amount,
            category = category,
            date = date,
            isDeleted =  false,
            createdAt = System.currentTimeMillis()
        )
        localDataSource.upsert(expense.toEntity())
        return id
    }

    override suspend fun update(id: String, name: String, amount: BigDecimal, category: String, date: Long) {
        val expense = get(id)?.copy(
            name = name,
            amount = amount,
            category = category,
            date = date
        ) ?: throw Exception("Expense (id $id) not found")

        localDataSource.upsert(expense.toEntity())
    }

    override suspend fun get(id: String): Expense? {
        return localDataSource.getById(id)?.toExternal()
    }
}
