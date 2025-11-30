package com.ivzb.urgat.data.weight

import com.ivzb.urgat.data.base.BaseRepository
import java.math.BigDecimal

interface WeightsRepository: BaseRepository<Weight> {

    suspend fun create(
        value: BigDecimal,
        date: Long
    ): String

}
