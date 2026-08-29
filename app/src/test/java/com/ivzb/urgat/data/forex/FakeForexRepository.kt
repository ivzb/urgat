package com.ivzb.urgat.data.forex

import com.ivzb.urgat.util.formatCurrency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.math.BigDecimal

class FakeForexRepository : ForexRepository {

    val rates = mutableMapOf<String, BigDecimal>()
    var shouldThrow = false

    /**
     * Currencies no rate can be resolved for.
     *
     * The real repository returns `null` rather than throwing when a quote is unreachable, which
     * is what silently drops amounts from the totals - so it has to be reproducible here.
     */
    val unresolvedCurrencies = mutableSetOf<String>()

    private val _refreshFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override val refreshFlow: Flow<Unit> = _refreshFlow.asSharedFlow()

    override suspend fun getExchangeRate(fromCurrency: String, toCurrency: String): BigDecimal? {
        if (shouldThrow) throw RuntimeException("Failed to fetch exchange rate")

        val from = fromCurrency.formatCurrency()
        val to = toCurrency.formatCurrency()

        if (from == to) return BigDecimal.ONE

        if (from in unresolvedCurrencies || to in unresolvedCurrencies) return null

        return rates["${from}${to}"] ?: throw RuntimeException("No rate for $from -> $to")
    }

    override suspend fun convert(amount: BigDecimal, fromCurrency: String, toCurrency: String): BigDecimal? {
        val rate = getExchangeRate(fromCurrency, toCurrency) ?: return null
        return amount * rate
    }

    override suspend fun clearCache() {
        rates.clear()
        _refreshFlow.emit(Unit)
    }

    suspend fun emitRefresh() {
        _refreshFlow.emit(Unit)
    }

}

