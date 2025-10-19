package com.ivzb.urgat.data.expense

import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface ExpensesRepository {

    fun getStream(): Flow<List<Expense>>

    suspend fun create(name: String, amount: BigDecimal): String

    suspend fun update(id: String, name: String, amount: BigDecimal)

    suspend fun get(id: String): Expense?

}