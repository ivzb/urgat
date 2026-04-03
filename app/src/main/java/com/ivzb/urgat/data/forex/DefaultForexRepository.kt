package com.ivzb.urgat.data.forex

import com.ivzb.urgat.data.marketPrice.MarketPriceRepository
import com.ivzb.urgat.di.ApplicationScope
import com.ivzb.urgat.util.Logger
import com.ivzb.urgat.util.Tracker
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.junit.Assert
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultForexRepository @Inject constructor(
    private val marketPriceRepository: MarketPriceRepository,
    private val logger: Logger,
    @ApplicationScope private val scope: CoroutineScope,
) : ForexRepository {

    private val _refreshFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override val refreshFlow: Flow<Unit> = _refreshFlow.asSharedFlow()

    private val cache = ConcurrentHashMap<String, Deferred<BigDecimal?>>()

    override suspend fun getExchangeRate(fromCurrency: String, toCurrency: String): BigDecimal? {
        val from = fromCurrency.uppercase()
        val to = toCurrency.uppercase()

        if (from == to) {
            return BigDecimal.ONE
        }

        val key = "${from}${to}"

        // ConcurrentHashMap.getOrPut is atomic. Meaning that only the first caller wins the put for a given key
        // and creates the Deferred (scope.async). All concurrent callers for the same key receive
        // the already-inserted Deferred and simply .await() the same single network result.
        return cache.getOrPut(key) {
            scope.async {
                val rate = marketPriceRepository.fetchPrice("${key}=X")?.price

                logger.debug("forex rate fetched: $from -> $to = $rate")

                if (rate == null) {
                    Tracker.error(Throwable("forex rate not found: $from -> $to"))
                } else {
                    val inverseKey = "${to}${from}"
                    val inverseRate = BigDecimal.ONE.divide(rate, 10, RoundingMode.HALF_UP)
                    cache.putIfAbsent(inverseKey, CompletableDeferred(inverseRate))
                    logger.debug("forex rate cached (inverse): $to -> $from = $inverseRate")
                }

                rate
            }
        }.await()
    }

    override suspend fun convert(
        amount: BigDecimal,
        fromCurrency: String,
        toCurrency: String,
    ): BigDecimal? {
        val rate = getExchangeRate(fromCurrency, toCurrency) ?: return null
        return amount * rate
    }

    override suspend fun clearCache() {
        cache.clear()
        _refreshFlow.emit(Unit)
    }

}