package com.ivzb.urgat.data.budget

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(
    tableName = "budgets",
    indices = [
        Index(value = ["isDeleted", "createdAt"]),
        Index(value = ["isDeleted", "date"]),
    ]
)
data class BudgetEntity(
    @PrimaryKey val id: String,
    val income: BigDecimal,
    val bankStart: BigDecimal,
    val bankEnd: BigDecimal?,
    val currency: String,
    val date: Long,
    val isDeleted: Boolean,
    val createdAt: Long
)