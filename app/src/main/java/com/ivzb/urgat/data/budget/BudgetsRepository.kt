package com.ivzb.urgat.data.budget

import com.ivzb.urgat.data.base.BaseRepository
import java.math.BigDecimal

interface BudgetsRepository: BaseRepository<Budget> {

    suspend fun create(income: BigDecimal, bankStart: BigDecimal, bankEnd: BigDecimal?, currency: String, date: Long): String

    suspend fun update(id: String, income: BigDecimal, bankStart: BigDecimal, bankEnd: BigDecimal?, currency: String, date: Long)

    suspend fun getLastUsedCurrency(): String?

}