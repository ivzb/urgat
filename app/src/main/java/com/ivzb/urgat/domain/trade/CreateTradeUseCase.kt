package com.ivzb.urgat.domain.trade

import com.ivzb.urgat.data.trade.TradeDao
import com.ivzb.urgat.data.trade.TradeEntity
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

interface CreateTradeUseCase {

    suspend operator fun invoke(
        name: String,
        amount: BigDecimal,
        currency: String,
        date: Long,
    )

}

class DefaultCreateTradeUseCase @Inject constructor(
    private val tradeDao: TradeDao
): CreateTradeUseCase {

    override suspend operator fun invoke(
        name: String,
        amount: BigDecimal,
        currency: String,
        date: Long,
    ) {
        val entity = TradeEntity(
            id = UUID.randomUUID().toString(),
            name = name,
            amount = amount,
            currency = currency,
            date = date,
            isDeleted = false,
            createdAt = System.currentTimeMillis(),
        )

        tradeDao.upsert(entity)
    }

}

