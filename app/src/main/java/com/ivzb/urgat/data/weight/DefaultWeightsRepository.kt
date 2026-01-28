package com.ivzb.urgat.data.weight

import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.util.Assert
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

class DefaultWeightsRepository @Inject constructor(
    private val localDataSource: WeightDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : WeightsRepository {

    override fun observe(limit: Int, dateRange: LongRange): Flow<List<Weight>> {
        return localDataSource.observeAll(limit, dateRange.first, dateRange.last).map { weights ->
            withContext(dispatcher) {
                weights.toExternal()
            }
        }
    }

    override fun observe(limit: Int, dateRange: LongRange, searchQuery: String): Flow<List<Weight>> {
        // searchQuery is not used at the moment
        return observe(limit, dateRange)
    }

    override suspend fun create(value: BigDecimal, date: Long): String {
        // ID creation might be a complex operation so it's executed using the supplied
        // coroutine dispatcher
        val id = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val weight = Weight(
            id = id,
            value = value,
            date = date,
            isDeleted = false,
            createdAt = System.currentTimeMillis()
        )
        localDataSource.upsert(weight.toEntity())
        return id
    }

    override suspend fun update(
        id: String,
        value: BigDecimal,
        date: Long,
    ) {
        val weight = get(id)?.copy(
            value = value,
            date = date,
        )

        if (weight == null) {
            Assert.fail("Investment (id $id) not found while update")
            return
        }

        localDataSource.upsert(weight.toEntity())
    }

    override suspend fun get(id: String): Weight? {
        return localDataSource.getById(id)?.toExternal()
    }

    override suspend fun delete(id: String) {
        localDataSource.deleteById(id)
    }
}
