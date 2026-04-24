package com.ivzb.urgat.domain.trade

import com.ivzb.urgat.data.trade.TradeDao
import javax.inject.Inject

interface GetLastUsedTradeFeeUseCase {

    suspend operator fun invoke(): String?

}

class DefaultGetLastUsedTradeFeeUseCase @Inject constructor(
    private val tradeDao: TradeDao
): GetLastUsedTradeFeeUseCase {

    override suspend fun invoke(): String? {
        return tradeDao.getLastUsedFee()
    }

}

