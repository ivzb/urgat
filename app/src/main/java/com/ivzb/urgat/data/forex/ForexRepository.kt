package com.ivzb.urgat.data.forex

import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface ForexRepository {

    val refreshFlow: Flow<Unit>

    suspend fun getExchangeRate(fromCurrency: String, toCurrency: String): BigDecimal?

    suspend fun convert(amount: BigDecimal, fromCurrency: String, toCurrency: String): BigDecimal?

    suspend fun clearCache()

}

