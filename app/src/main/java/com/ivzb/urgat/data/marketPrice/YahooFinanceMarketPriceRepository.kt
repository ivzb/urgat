package com.ivzb.urgat.data.marketPrice

import com.ivzb.urgat.di.ApplicationScope
import com.ivzb.urgat.di.IoDispatcher
import com.ivzb.urgat.util.Logger
import com.ivzb.urgat.util.TimeProvider
import com.ivzb.urgat.util.Tracker
import com.ivzb.urgat.util.normalizeInstrument
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class YahooFinanceMarketPriceRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val api: YahooFinanceApi,
    private val logger: Logger,
    private val timeProvider: TimeProvider,
    @ApplicationScope private val scope: CoroutineScope,
) : MarketPriceRepository {

    private val _refreshFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override val refreshFlow: Flow<Unit> = _refreshFlow.asSharedFlow()

    /** Fetched prices, kept until [clearCache]. Only ever holds successful fetches. */
    private val cache = ConcurrentHashMap<String, Deferred<MarketPrice?>>()

    /** Symbols which failed recently, so they are not asked for again on every single update. */
    private val failures = ConcurrentHashMap<String, Failure>()

    @Volatile
    private var fetchedAt: Long? = null

    override val lastFetchedAt: Long? get() = fetchedAt

    override suspend fun fetchPrice(symbol: String): MarketPrice? {
        val symbol = normalizeInstrument(symbol.uppercase())

        if (isBackingOff(symbol)) {
            logger.debug("market price fetch skipped while backing off: $symbol")
            return null
        }

        val attempt = cache.getOrPut(symbol) {
            scope.async {
                withContext(dispatcher) {
                    try {
                        val meta = api.fetch(symbol).chart.result.first().meta

                        MarketPrice(
                            symbol = symbol,
                            description = meta.shortName,
                            price = BigDecimal(meta.regularMarketPrice),
                            currency = meta.currency,
                            previousClosePrice = BigDecimal(meta.chartPreviousClose),
                            pricePrecision = meta.priceHint,
                        ).also {
                            fetchedAt = timeProvider.currentTimeMillis()
                            logger.debug("market price fetched: $it")
                        }
                    } catch (e: Exception) {
                        logger.debug("market price fetch failed: $symbol")
                        Tracker.error(e)
                        null
                    }
                }
            }
        }

        val marketPrice = attempt.await()

        if (marketPrice == null) {
            // a failure must never stay in the cache - it would turn one unreachable moment into
            // a permanently wrong widget which only a manual refresh could ever clear.
            // only this exact attempt is dropped, a newer one may already be in flight
            cache.remove(symbol, attempt)
            recordFailure(symbol)
        } else {
            failures.remove(symbol)
        }

        return marketPrice
    }

    override suspend fun clearCache() {
        cache.clear()
        // an explicit refresh means "try right now", so the backoff goes with it
        failures.clear()
        _refreshFlow.emit(Unit)
    }

    /**
     * Whether [symbol] failed recently enough that it is not worth asking again yet.
     *
     * Without this a dropped connection would be retried on every widget update and every
     * database change, which is the sort of burst that earns a rate limit.
     */
    private fun isBackingOff(symbol: String): Boolean {
        val failure = failures[symbol] ?: return false

        return timeProvider.currentTimeMillis() - failure.at < backoffMillis(failure.count)
    }

    private fun recordFailure(symbol: String) {
        failures.compute(symbol) { _, previous ->
            Failure(
                count = (previous?.count ?: 0) + 1,
                at = timeProvider.currentTimeMillis(),
            )
        }
    }

    /**
     * Doubles per consecutive failure, capped well below the widget update period so a background
     * update always ends up retrying and the data heals itself without anyone tapping anything.
     */
    private fun backoffMillis(failures: Int): Long {
        val doublings = (failures - 1).coerceIn(0, MAX_BACKOFF_DOUBLINGS)

        return (INITIAL_BACKOFF_MILLIS shl doublings).coerceAtMost(MAX_BACKOFF_MILLIS)
    }

    private data class Failure(

        /** How many times in a row the symbol failed. */
        val count: Int,

        /** When it last did. */
        val at: Long,
    )

    companion object {

        internal const val INITIAL_BACKOFF_MILLIS = 30_000L

        /** Comfortably under the widget's 30 minute update period. */
        internal const val MAX_BACKOFF_MILLIS = 10 * 60_000L

        /** Only guards the shift from overflowing, [MAX_BACKOFF_MILLIS] is the real limit. */
        private const val MAX_BACKOFF_DOUBLINGS = 10

    }

}
