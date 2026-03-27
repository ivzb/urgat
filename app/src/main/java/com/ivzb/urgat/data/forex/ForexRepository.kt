package com.ivzb.urgat.data.forex

import java.math.BigDecimal

interface ForexRepository {

    suspend fun getExchangeRate(fromCurrency: String, toCurrency: String): BigDecimal?

    suspend fun convert(amount: BigDecimal, fromCurrency: String, toCurrency: String): BigDecimal?

}

