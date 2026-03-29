package com.ivzb.urgat.data.watchlist

import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.util.Assert
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class DefaultWatchlistAssetsRepository @Inject constructor(
    private val localDataSource: WatchlistAssetDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : WatchlistAssetsRepository {

    override fun observe(limit: Int, dateRange: LongRange): Flow<List<WatchlistAsset>> {
        return localDataSource.observeAll(limit, dateRange.first, dateRange.last).map { assets ->
            withContext(dispatcher) {
                assets.toExternal()
            }
        }
    }

    override fun observe(limit: Int, dateRange: LongRange, searchQuery: String): Flow<List<WatchlistAsset>> {
        return localDataSource.observeAll(limit, dateRange.first, dateRange.last, searchQuery).map { assets ->
            withContext(dispatcher) {
                assets.toExternal()
            }
        }
    }

    override suspend fun create(
        name: String,
    ): String {
        val id = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val watchlistAsset = WatchlistAsset(
            id = id,
            name = name,
            isDeleted = false,
            createdAt = System.currentTimeMillis()
        )
        localDataSource.upsert(watchlistAsset.toEntity())
        return id
    }

    override suspend fun update(
        id: String,
        name: String,
    ) {
        val watchlistAsset = get(id)?.copy(
            name = name,
        )

        if (watchlistAsset == null) {
            Assert.fail("WatchlistAsset (id $id) not found while update")
            return
        }

        localDataSource.upsert(watchlistAsset.toEntity())
    }

    override suspend fun get(id: String): WatchlistAsset? {
        return localDataSource.getById(id)?.toExternal()
    }

    override suspend fun delete(id: String) {
        localDataSource.deleteById(id)
    }
}