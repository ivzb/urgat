package com.ivzb.urgat.domain.trade

import com.ivzb.urgat.data.trade.TradeDao
import com.ivzb.urgat.data.trade.TradeEntity
import java.math.BigDecimal
import javax.inject.Inject

interface UpdateTradeUseCase {

    suspend operator fun invoke(
        id: String,
        name: String,
        shares: BigDecimal,
        fee: BigDecimal,
        marketPrice: BigDecimal,
        currency: String,
        orderType: String,
        date: Long,
    )

}

class DefaultUpdateTradeUseCase @Inject constructor(
    private val tradeDao: TradeDao
): UpdateTradeUseCase {

    override suspend operator fun invoke(
        id: String,
        name: String,
        shares: BigDecimal,
        fee: BigDecimal,
        marketPrice: BigDecimal,
        currency: String,
        orderType: String,
        date: Long,
    ) {
        val existing = tradeDao.getById(id) ?: return

        val entity = TradeEntity(
            id = id,
            name = name,
            shares = shares,
            fee = fee,
            marketPrice = marketPrice,
            currency = currency,
            orderType = orderType,
            date = date,
            isDeleted = existing.isDeleted,
            createdAt = existing.createdAt,
        )

        tradeDao.upsert(entity)
    }

}

