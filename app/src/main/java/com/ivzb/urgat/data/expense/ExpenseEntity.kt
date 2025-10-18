package com.ivzb.urgat.data.expense

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val name: String,
    val amount: BigDecimal,
    var isDeleted: Boolean,
    var createdAt: Long
)