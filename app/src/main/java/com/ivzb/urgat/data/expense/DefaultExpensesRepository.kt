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

    override suspend fun create(name: String, amount: BigDecimal): String {
        // ID creation might be a complex operation so it's executed using the supplied
        // coroutine dispatcher
        val id = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val todo = Expense(
            id = id,
            name = name,
            amount = amount,
            createdAt = System.currentTimeMillis()
        )
        localDataSource.upsert(todo.toEntity())
        return id
    }

    override suspend fun update(id: String, name: String, amount: BigDecimal) {
        val todo = get(id)?.copy(
            name = name,
            amount = amount
        ) ?: throw Exception("Todo (id $id) not found")

        localDataSource.upsert(todo.toEntity())
    }

    override suspend fun get(id: String): Expense? {
        return localDataSource.getById(id)?.toExternal()
    }
}
