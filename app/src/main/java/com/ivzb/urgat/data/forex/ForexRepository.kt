package com.ivzb.urgat.data.forex

import java.math.BigDecimal
import kotlinx.coroutines.flow.Flow

interface ForexRepository {

    val refreshFlow: Flow<Unit>

    suspend fun getExchangeRate(fromCurrency: String, toCurrency: String): BigDecimal?

    suspend fun convert(amount: BigDecimal, fromCurrency: String, toCurrency: String): BigDecimal?

    suspend fun clearCache()

}
