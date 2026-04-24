package com.ivzb.urgat.domain.trade

import com.ivzb.urgat.data.trade.TradeDao
import com.ivzb.urgat.data.trade.TradeEntity
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

interface CreateTradeUseCase {

    suspend operator fun invoke(
        name: String,
        shares: BigDecimal,
        fee: BigDecimal,
        marketPrice: BigDecimal,
        currency: String,
        orderType: String,
        date: Long,
    )

}

class DefaultCreateTradeUseCase @Inject constructor(
    private val tradeDao: TradeDao
): CreateTradeUseCase {

    override suspend operator fun invoke(
        name: String,
        shares: BigDecimal,
        fee: BigDecimal,
        marketPrice: BigDecimal,
        currency: String,
        orderType: String,
        date: Long,
    ) {
        val entity = TradeEntity(
            id = UUID.randomUUID().toString(),
            name = name,
            shares = shares,
            fee = fee,
            marketPrice = marketPrice,
            currency = currency,
            orderType = orderType,
            date = date,
            isDeleted = false,
            createdAt = System.currentTimeMillis(),
        )

        tradeDao.upsert(entity)
    }

}

