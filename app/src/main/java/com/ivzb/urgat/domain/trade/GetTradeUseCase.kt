package com.ivzb.urgat.domain.trade

import com.ivzb.urgat.data.trade.Trade
import com.ivzb.urgat.data.trade.TradeDao
import com.ivzb.urgat.data.trade.toExternal
import javax.inject.Inject

interface GetTradeUseCase {

    suspend operator fun invoke(id: String): Trade?

}

class DefaultGetTradeUseCase @Inject constructor(
    private val tradeDao: TradeDao
): GetTradeUseCase {

    override suspend fun invoke(id: String): Trade? {
        return tradeDao.getById(id)?.toExternal(pricePrecision = null)
    }

}

