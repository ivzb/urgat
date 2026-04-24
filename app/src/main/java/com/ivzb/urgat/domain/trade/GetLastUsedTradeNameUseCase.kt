package com.ivzb.urgat.domain.trade

import com.ivzb.urgat.data.trade.TradeDao
import javax.inject.Inject

interface GetLastUsedTradeNameUseCase {

    suspend operator fun invoke(): String?

}

class DefaultGetLastUsedTradeNameUseCase @Inject constructor(
    private val tradeDao: TradeDao
): GetLastUsedTradeNameUseCase {

    override suspend fun invoke(): String? {
        return tradeDao.getLastUsedName()
    }

}

