package com.ivzb.urgat.data.watchlist

import com.ivzb.urgat.data.marketPrice.MarketPriceRepository
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.util.Assert
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class DefaultWatchlistAssetsRepository @Inject constructor(
    private val localDataSource: WatchlistAssetDao,
    private val marketPriceRepository: MarketPriceRepository,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : WatchlistAssetsRepository {

    private val _isRefreshingFlow = MutableStateFlow(false)
    override val isRefreshingFlow: StateFlow<Boolean> = _isRefreshingFlow.asStateFlow()

    private var refreshStartTime = 0L

    override fun observe(limit: Int, dateRange: LongRange): Flow<List<WatchlistAsset>> {
        return marketPriceRepository.refreshFlow
            .onStart { emit(Unit) }
            .flatMapLatest {
                _isRefreshingFlow.value = true
                refreshStartTime = System.currentTimeMillis()

                localDataSource.observeAll(limit, dateRange.first, dateRange.last).map { assets ->
                    withContext(dispatcher) {
                        assets.toExternal()
                    }
                }.onEach {
                    ensureMinRefreshDuration()
                    _isRefreshingFlow.value = false
                }
            }
    }

    override fun observe(limit: Int, dateRange: LongRange, searchQuery: String): Flow<List<WatchlistAsset>> {
        return marketPriceRepository.refreshFlow
            .onStart { emit(Unit) }
            .flatMapLatest {
                _isRefreshingFlow.value = true
                refreshStartTime = System.currentTimeMillis()

                localDataSource.observeAll(limit, dateRange.first, dateRange.last, searchQuery).map { assets ->
                    withContext(dispatcher) {
                        assets.toExternal()
                    }
                }.onEach {
                    ensureMinRefreshDuration()
                    _isRefreshingFlow.value = false
                }
            }
    }

    private suspend fun ensureMinRefreshDuration() {
        val elapsed = System.currentTimeMillis() - refreshStartTime

        if (elapsed < MIN_REFRESH_DURATION_MS) {
            delay(MIN_REFRESH_DURATION_MS - elapsed)
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
            description = null,
            marketPrice = null,
            currency = null,
            previousClosePrice = null,
            pricePrecision = null,
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

    private suspend fun List<WatchlistAssetEntity>.toExternal(): List<WatchlistAsset> {
        return coroutineScope {
            map { async { it.toExternal() } }.awaitAll()
        }
    }

    private suspend fun WatchlistAssetEntity.toExternal(): WatchlistAsset {
        return toExternal(
            marketPrice = marketPriceRepository.fetchPrice(name),
        )
    }

    companion object {

        private const val MIN_REFRESH_DURATION_MS = 1_000L

    }

}