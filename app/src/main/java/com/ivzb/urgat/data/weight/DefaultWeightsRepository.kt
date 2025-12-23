package com.ivzb.urgat.data.weight

import android.database.Cursor
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

class DefaultWeightsRepository @Inject constructor(
    private val localDataSource: WeightDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) : WeightsRepository {

    override fun observe(limit: Int): Flow<List<Weight>> {
        return localDataSource.observeAll(limit).map { weights ->
            withContext(dispatcher) {
                weights.toExternal()
            }
        }
    }

    override fun observe(limit: Int, searchQuery: String): Flow<List<Weight>> {
        // searchQuery is not used at the moment
        return observe(limit)
    }

    override fun stream(): Cursor {
        return localDataSource.streamAll()
    }

    override suspend fun create(value: BigDecimal, date: Long): String {
        // ID creation might be a complex operation so it's executed using the supplied
        // coroutine dispatcher
        val id = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val note = Weight(
            id = id,
            value = value,
            date = date,
            isDeleted = false,
            createdAt = System.currentTimeMillis()
        )
        localDataSource.upsert(note.toEntity())
        return id
    }

    override suspend fun get(id: String): Weight? {
        return localDataSource.getById(id)?.toExternal()
    }

    override suspend fun delete(id: String) {
        localDataSource.deleteById(id)
    }
}
