package com.ivzb.urgat.data.saving

import com.ivzb.urgat.data.base.BaseRepository
import java.math.BigDecimal

interface SavingsRepository: BaseRepository<Saving> {

    suspend fun create(
        name: String,
        amount: BigDecimal,
        date: Long,
    ): String

    suspend fun update(
        id: String,
        name: String,
        amount: BigDecimal,
        date: Long,
    )

}