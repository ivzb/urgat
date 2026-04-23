package com.ivzb.urgat.domain.trade

import com.ivzb.urgat.data.trade.TradeDao
import javax.inject.Inject

interface DeleteTradeUseCase {

    suspend operator fun invoke(id: String)

}

class DefaultDeleteTradeUseCase @Inject constructor(
    private val tradeDao: TradeDao
): DeleteTradeUseCase {

    override suspend operator fun invoke(id: String) {
        tradeDao.deleteById(id)
    }

}

