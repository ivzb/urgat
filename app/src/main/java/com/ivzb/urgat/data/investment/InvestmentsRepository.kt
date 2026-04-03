package com.ivzb.urgat.data.investment

import com.ivzb.urgat.data.base.BaseRepository
import kotlinx.coroutines.flow.StateFlow
import java.math.BigDecimal

interface InvestmentsRepository: BaseRepository<Investment> {

    val isRefreshingFlow: StateFlow<Boolean>

    suspend fun create(
        name: String,
        shares: BigDecimal,
        costPrice: BigDecimal,
        category: String,
        currency: String,
        date: Long,
    ): String

    suspend fun update(
        id: String,
        name: String,
        shares: BigDecimal,
        costPrice: BigDecimal,
        category: String,
        currency: String,
        date: Long,
    )

}