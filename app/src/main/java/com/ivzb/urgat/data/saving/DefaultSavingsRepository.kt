package com.ivzb.urgat.data.saving

import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.util.Assert
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

class DefaultSavingsRepository @Inject constructor(
    private val localDataSource: SavingDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : SavingsRepository {

    override fun observe(limit: Int, dateRange: LongRange): Flow<List<Saving>> {
        return localDataSource.observeAll(limit, dateRange.first, dateRange.last).map { savings ->
            withContext(dispatcher) {
                savings.toExternal()
            }
        }
    }

    override fun observe(limit: Int, dateRange: LongRange, searchQuery: String): Flow<List<Saving>> {
        return localDataSource.observeAll(limit, dateRange.first, dateRange.last, searchQuery).map { savings ->
            withContext(dispatcher) {
                savings.toExternal()
            }
        }
    }

    override suspend fun create(
        name: String,
        amount: BigDecimal,
        currency: String,
        date: Long,
    ): String {
        // ID creation might be a complex operation so it's executed using the supplied
        // coroutine dispatcher
        val id = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val saving = Saving(
            id = id,
            name = name,
            amount = amount,
            currency = currency,
            date = date,
            isDeleted = false,
            createdAt = System.currentTimeMillis()
        )
        localDataSource.upsert(saving.toEntity())
        return id
    }

    override suspend fun update(
        id: String,
        name: String,
        amount: BigDecimal,
        currency: String,
        date: Long,
    ) {
        val saving = get(id)?.copy(
            name = name,
            amount = amount,
            currency = currency,
            date = date,
        )

        if (saving == null) {
            Assert.fail("Saving (id $id) not found while update")
            return
        }

        localDataSource.upsert(saving.toEntity())
    }

    override suspend fun get(id: String): Saving? {
        return localDataSource.getById(id)?.toExternal()
    }

    override suspend fun delete(id: String) {
        localDataSource.deleteById(id)
    }

    override suspend fun getLastUsedCurrency(): String? {
        return localDataSource.getLastUsedCurrency()
    }
}
