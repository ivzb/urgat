package com.ivzb.urgat.data.expense

import com.ivzb.urgat.data.base.BaseRepository
import java.math.BigDecimal

interface ExpensesRepository: BaseRepository<Expense> {

    suspend fun create(name: String, amount: BigDecimal, category: String, date: Long, excludeFromAnalytics: Boolean): String

    suspend fun update(id: String, name: String, amount: BigDecimal, category: String, date: Long, excludeFromAnalytics: Boolean)

}