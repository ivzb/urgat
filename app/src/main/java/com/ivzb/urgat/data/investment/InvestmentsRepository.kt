package com.ivzb.urgat.data.investment

import com.ivzb.urgat.data.base.BaseRepository
import java.math.BigDecimal

interface InvestmentsRepository: BaseRepository<Investment> {

    suspend fun create(name: String, currency: String, amount: BigDecimal, category: String): String

    suspend fun update(id: String, name: String, currency: String, amount: BigDecimal, category: String)

}