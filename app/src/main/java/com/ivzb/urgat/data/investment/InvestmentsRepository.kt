package com.ivzb.urgat.data.investment

import com.ivzb.urgat.data.base.BaseRepository
import java.math.BigDecimal

interface InvestmentsRepository: BaseRepository<Investment> {

    suspend fun create(
        name: String,
        shares: BigDecimal,
        cost: BigDecimal,
        category: String,
        currency: String,
        date: Long,
    ): String

    suspend fun update(
        id: String,
        name: String,
        shares: BigDecimal,
        cost: BigDecimal,
        category: String,
        currency: String,
        date: Long,
    )

}