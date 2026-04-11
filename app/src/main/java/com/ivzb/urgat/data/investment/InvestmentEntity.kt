package com.ivzb.urgat.data.investment

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(
    tableName = "investments",
    indices = [
        Index(value = ["isDeleted", "createdAt"]),
        Index(value = ["isDeleted", "date"]),
    ]
)
data class InvestmentEntity(
    @PrimaryKey val id: String,
    val name: String,
    val shares: BigDecimal,
    val cost: BigDecimal,
    val category: String,
    val currency: String,
    val date: Long,
    val isDeleted: Boolean,
    val createdAt: Long
)