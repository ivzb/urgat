package com.ivzb.urgat.domain.trade

import com.ivzb.urgat.data.trade.TradeDao
import com.ivzb.urgat.data.trade.TradeEntity
import java.math.BigDecimal
import javax.inject.Inject

interface UpdateTradeUseCase {

    suspend operator fun invoke(
        id: String,
        name: String,
        amount: BigDecimal,
        currency: String,
        date: Long,
    )

}

class DefaultUpdateTradeUseCase @Inject constructor(
    private val tradeDao: TradeDao
): UpdateTradeUseCase {

    override suspend operator fun invoke(
        id: String,
        name: String,
        amount: BigDecimal,
        currency: String,
        date: Long,
    ) {
        val existing = tradeDao.getById(id) ?: return

        val entity = TradeEntity(
            id = id,
            name = name,
            amount = amount,
            currency = currency,
            date = date,
            isDeleted = existing.isDeleted,
            createdAt = existing.createdAt,
        )

        tradeDao.upsert(entity)
    }

}

