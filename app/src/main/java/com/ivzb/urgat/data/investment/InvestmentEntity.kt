package com.ivzb.urgat.data.investment

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "investments")
data class InvestmentEntity(
    @PrimaryKey val id: String,
    val name: String,
    val currency: String,
    val amount: BigDecimal,
    val category: String,
    var isDeleted: Boolean,
    var createdAt: Long
)