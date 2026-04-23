package com.ivzb.urgat.domain.trade

import com.ivzb.urgat.data.trade.TradeDao
import javax.inject.Inject

interface GetLastUsedTradeCurrencyUseCase {

    suspend operator fun invoke(): String?

}

class DefaultGetLastUsedTradeCurrencyUseCase @Inject constructor(
    private val tradeDao: TradeDao
): GetLastUsedTradeCurrencyUseCase {

    override suspend fun invoke(): String? {
        return tradeDao.getLastUsedCurrency()
    }

}

