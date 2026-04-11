package com.ivzb.urgat.data.weight

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(
    tableName = "weights",
    indices = [
        Index(value = ["isDeleted", "date"]),
    ]
)
data class WeightEntity(
    @PrimaryKey val id: String,
    val value: BigDecimal,
    val date: Long,
    val isDeleted: Boolean,
    val createdAt: Long
)