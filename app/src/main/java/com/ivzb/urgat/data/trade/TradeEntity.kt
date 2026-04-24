package com.ivzb.urgat.data.trade

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(
    tableName = "trade",
    indices = [
        Index(value = ["isDeleted", "createdAt"]),
        Index(value = ["isDeleted", "date"]),
    ]
)
data class TradeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val shares: BigDecimal,
    val fee: BigDecimal,
    val marketPrice: BigDecimal,
    val currency: String,
    val orderType: String,
    val date: Long,
    val isDeleted: Boolean,
    val createdAt: Long
)