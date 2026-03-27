package com.ivzb.urgat.data.forex

import com.ivzb.urgat.data.marketPrice.MarketPriceRepository
import com.ivzb.urgat.di.ApplicationScope
import com.ivzb.urgat.util.Logger
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
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

                val inverseKey = "${to}${from}"
                val inverseRate = BigDecimal.ONE.divide(rate, 10, RoundingMode.HALF_UP)
                cache.putIfAbsent(inverseKey, CompletableDeferred(inverseRate))
                logger.debug("forex rate cached (inverse): $to -> $from = $inverseRate")

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

}





