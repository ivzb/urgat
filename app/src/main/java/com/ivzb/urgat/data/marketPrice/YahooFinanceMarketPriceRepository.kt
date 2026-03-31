package com.ivzb.urgat.data.marketPrice

import com.ivzb.urgat.di.ApplicationScope
import com.ivzb.urgat.di.IoDispatcher
import com.ivzb.urgat.util.Logger
import com.ivzb.urgat.util.Tracker
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class YahooFinanceMarketPriceRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val api: YahooFinanceApi,
    private val logger: Logger,
    @ApplicationScope private val scope: CoroutineScope,
) : MarketPriceRepository {

    private val cache = ConcurrentHashMap<String, Deferred<MarketPrice?>>()

    override suspend fun fetchPrice(symbol: String): MarketPrice? {
        val symbol = symbol.uppercase()

        return cache.getOrPut(symbol) {
            scope.async {
                withContext(dispatcher) {
                    try {
                        val meta = api.fetch(
                            yahooSymbolMapping[symbol] ?: symbol
                        ).chart.result.first().meta

                        MarketPrice(
                            symbol = symbol,
                            description = meta.shortName,
                            price = BigDecimal(meta.regularMarketPrice),
                            currency = meta.currency,
                            previousClosePrice = BigDecimal(meta.chartPreviousClose),
                        ).also {
                            logger.debug("market price fetched: $it")
                        }
                    } catch (e: Exception) {
                        logger.debug("market price fetch failed: $symbol")
                        Tracker.error(e)
                        null
                    }
                }
            }
        }.await()
    }

    companion object {

        // replace user symbol with yahoo finance symbol
        private val yahooSymbolMapping = mapOf(
            "BRK.B" to "BRK-B",
            "SXR8" to "SXR8.DE",
            "XAU" to "GC=F",
            "BTC" to "BTC-USD",
            "ETH" to "ETH-USD",
        )

    }

}
