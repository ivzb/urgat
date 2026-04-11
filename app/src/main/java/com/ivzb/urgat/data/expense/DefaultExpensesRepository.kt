package com.ivzb.urgat.data.expense

import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.util.Assert
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

class DefaultExpensesRepository @Inject constructor(
    private val localDataSource: ExpenseDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : ExpensesRepository {

    override fun observe(limit: Int, dateRange: LongRange): Flow<List<Expense>> {
        return localDataSource.observeAll(limit, dateRange.first, dateRange.last).map { expenses ->
            withContext(dispatcher) {
                expenses.toExternal()
            }
        }
    }

    override fun observe(limit: Int, dateRange: LongRange, searchQuery: String): Flow<List<Expense>> {
        return localDataSource.observeAll(limit, dateRange.first, dateRange.last, searchQuery).map { expenses ->
            withContext(dispatcher) {
                expenses.toExternal()
            }
        }
    }

    override suspend fun create(
        name: String,
        amount: BigDecimal,
        currency: String,
        category: String,
        date: Long,
        excludeFromAnalytics: Boolean,
    ): String {
        // ID creation might be a complex operation so it's executed using the supplied
        // coroutine dispatcher
        val id = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val expense = Expense(
            id = id,
            name = name,
            amount = amount,
            currency = currency,
            category = category,
            date = date,
            excludeFromAnalytics = excludeFromAnalytics,
            isDeleted = false,
            createdAt = System.currentTimeMillis()
        )
        localDataSource.upsert(expense.toEntity())
        return id
    }

    override suspend fun update(
        id: String,
        name: String,
        amount: BigDecimal,
        currency: String,
        category: String,
        date: Long,
        excludeFromAnalytics: Boolean,
    ) {
        val expense = get(id)?.copy(
            name = name,
            amount = amount,
            currency = currency,
            category = category,
            date = date,
            excludeFromAnalytics = excludeFromAnalytics,
        )

        if (expense == null) {
            Assert.fail("Expense (id $id) not found while update")
            return
        }

        localDataSource.upsert(expense.toEntity())
    }

    override suspend fun get(id: String): Expense? {
        return localDataSource.getById(id)?.toExternal()
    }

    override suspend fun delete(id: String) {
        localDataSource.deleteById(id)
    }

    override suspend fun getLastUsedCurrency(): String? {
        return localDataSource.getLastUsedCurrency()
    }
}
