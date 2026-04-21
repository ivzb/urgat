package com.ivzb.urgat.data.trading

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(
    tableName = "trading",
    indices = [
        Index(value = ["isDeleted", "createdAt"]),
        Index(value = ["isDeleted", "date"]),
    ]
)
data class TradingEntity(
    @PrimaryKey val id: String,
    val name: String,
    val shares: BigDecimal,
    val marketPrice: BigDecimal,
    val fee: BigDecimal,
    val currency: String,
    val orderType: String,
    val date: Long,
    val isDeleted: Boolean,
    val createdAt: Long
)