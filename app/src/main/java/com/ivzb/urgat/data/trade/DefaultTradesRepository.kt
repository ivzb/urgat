package com.ivzb.urgat.data.trade

import com.ivzb.urgat.data.marketPrice.MarketPriceRepository
import com.ivzb.urgat.di.DefaultDispatcher
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
import javax.inject.Inject

class DefaultTradesRepository @Inject constructor(
    private val localDataSource: TradeDao,
    private val marketPriceRepository: MarketPriceRepository,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : TradesRepository {

    private val _isRefreshingFlow = MutableStateFlow(false)
    override val isRefreshingFlow: StateFlow<Boolean> = _isRefreshingFlow.asStateFlow()

    private var refreshStartTime = 0L
    private var isInitialLoad = true

    override fun observe(limit: Int, dateRange: LongRange): Flow<List<Trade>> {
        isInitialLoad = true

        return marketPriceRepository.refreshFlow
            .onStart { emit(Unit) }
            .flatMapLatest {
                if (!isInitialLoad) {
                    _isRefreshingFlow.value = true
                    refreshStartTime = System.currentTimeMillis()
                }

                isInitialLoad = false

                localDataSource.observeAll(limit, dateRange.first, dateRange.last).map { trades ->
                    withContext(dispatcher) {
                        trades.toExternal()
                    }
                }.onEach {
                    if (_isRefreshingFlow.value) {
                        ensureMinRefreshDuration()
                        _isRefreshingFlow.value = false
                    }
                }
            }
    }

    override fun observe(limit: Int, dateRange: LongRange, searchQuery: String): Flow<List<Trade>> {
        isInitialLoad = true

        return marketPriceRepository.refreshFlow
            .onStart { emit(Unit) }
            .flatMapLatest {
                if (!isInitialLoad) {
                    _isRefreshingFlow.value = true
                    refreshStartTime = System.currentTimeMillis()
                }

                isInitialLoad = false

                localDataSource.observeAll(limit, dateRange.first, dateRange.last, searchQuery).map { trades ->
                    withContext(dispatcher) {
                        trades.toExternal()
                    }
                }.onEach {
                    if (_isRefreshingFlow.value) {
                        ensureMinRefreshDuration()
                        _isRefreshingFlow.value = false
                    }
                }
            }
    }

    private suspend fun ensureMinRefreshDuration() {
        val elapsed = System.currentTimeMillis() - refreshStartTime

        if (elapsed < MIN_REFRESH_DURATION_MS) {
            delay(MIN_REFRESH_DURATION_MS - elapsed)
        }
    }

    override suspend fun get(id: String): Trade? {
        return localDataSource.getById(id)?.toExternal(pricePrecision = null)
    }

    override suspend fun delete(id: String) {
        localDataSource.deleteById(id)
    }

    private suspend fun List<TradeEntity>.toExternal(): List<Trade> {
        return coroutineScope { map { async { it.toExternal() } }.awaitAll() }
    }

    private suspend fun TradeEntity.toExternal(): Trade {
        val marketPriceData = marketPriceRepository.fetchPrice(name)

        return toExternal(
            pricePrecision = marketPriceData?.pricePrecision,
        )
    }

    companion object {

        private const val MIN_REFRESH_DURATION_MS = 1_000L

    }

}

