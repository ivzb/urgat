package com.ivzb.urgat.data.expense

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val name: String,
    val amount: BigDecimal,
    val currency: String,
    val category: String,
    val date: Long,
    val excludeFromAnalytics: Boolean,
    val isDeleted: Boolean,
    val createdAt: Long
)