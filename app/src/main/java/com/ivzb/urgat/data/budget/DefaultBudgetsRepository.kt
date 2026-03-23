package com.ivzb.urgat.data.budget

import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.util.Assert
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

class DefaultBudgetsRepository @Inject constructor(
    private val localDataSource: BudgetDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : BudgetsRepository {

    override fun observe(limit: Int, dateRange: LongRange): Flow<List<Budget>> {
        return localDataSource.observeAll(limit, dateRange.first, dateRange.last).map { budgets ->
            withContext(dispatcher) {
                budgets.toExternal()
            }
        }
    }

    override fun observe(limit: Int, dateRange: LongRange, searchQuery: String): Flow<List<Budget>> {
        // searchQuery is not used at the moment
        return observe(limit, dateRange)
    }

    override suspend fun create(
        income: BigDecimal,
        bankStart: BigDecimal,
        bankEnd: BigDecimal?,
        currency: String,
        date: Long,
    ): String {
        // ID creation might be a complex operation so it's executed using the supplied
        // coroutine dispatcher
        val id = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val budget = Budget(
            id = id,
            income = income,
            bankStart = bankStart,
            bankEnd = bankEnd,
            currency = currency,
            date = date,
            isDeleted = false,
            createdAt = System.currentTimeMillis()
        )
        localDataSource.upsert(budget.toEntity())
        return id
    }

    override suspend fun update(
        id: String,
        income: BigDecimal,
        bankStart: BigDecimal,
        bankEnd: BigDecimal?,
        currency: String,
        date: Long,
    ) {
        val budget = get(id)?.copy(
            income = income,
            bankStart = bankStart,
            bankEnd = bankEnd,
            currency = currency,
            date = date,
        )

        if (budget == null) {
            Assert.fail("Budget (id $id) not found while update")
            return
        }

        localDataSource.upsert(budget.toEntity())
    }

    override suspend fun get(id: String): Budget? {
        return localDataSource.getById(id)?.toExternal()
    }

    override suspend fun delete(id: String) {
        localDataSource.deleteById(id)
    }
}
