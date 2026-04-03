package com.ivzb.urgat.data.marketPrice

import com.ivzb.urgat.di.ApplicationScope
import com.ivzb.urgat.di.IoDispatcher
import com.ivzb.urgat.util.Logger
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
    @ApplicationScope private val scope: CoroutineScope,
) : MarketPriceRepository {

    private val cache = ConcurrentHashMap<String, Deferred<MarketPrice?>>()

    private val _refreshFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override val refreshFlow: Flow<Unit> = _refreshFlow.asSharedFlow()

    override suspend fun fetchPrice(symbol: String): MarketPrice? {
        val symbol = normalizeInstrument(symbol.uppercase())

        return cache.getOrPut(symbol) {
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

    override suspend fun clearCache() {
        cache.clear()
        _refreshFlow.emit(Unit)
    }

}
