package com.ivzb.urgat.data.forex

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.math.BigDecimal

class FakeForexRepository : ForexRepository {

    val rates = mutableMapOf<String, BigDecimal>()
    var shouldThrow = false

    private val _refreshFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override val refreshFlow: Flow<Unit> = _refreshFlow.asSharedFlow()

    override suspend fun getExchangeRate(fromCurrency: String, toCurrency: String): BigDecimal {
        if (shouldThrow) throw RuntimeException("Failed to fetch exchange rate")

        val from = fromCurrency.uppercase()
        val to = toCurrency.uppercase()

        if (from == to) return BigDecimal.ONE

        return rates["${from}${to}"] ?: throw RuntimeException("No rate for $from -> $to")
    }

    override suspend fun convert(amount: BigDecimal, fromCurrency: String, toCurrency: String): BigDecimal {
        val rate = getExchangeRate(fromCurrency, toCurrency)
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

