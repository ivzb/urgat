package com.ivzb.urgat.data.budget

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey val id: String,
    val income: BigDecimal,
    val bankStart: BigDecimal,
    val bankEnd: BigDecimal?,
    val date: Long,
    val isDeleted: Boolean,
    val createdAt: Long
)