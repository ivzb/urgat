package com.ivzb.urgat.data.weight

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "weights")
data class WeightEntity(
    @PrimaryKey val id: String,
    val value: BigDecimal,
    val isDeleted: Boolean,
    val createdAt: Long
)