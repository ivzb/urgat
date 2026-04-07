package com.ivzb.urgat.data.saving

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "savings")
data class SavingEntity(
    @PrimaryKey val id: String,
    val name: String,
    val amount: BigDecimal,
    val currency: String,
    val date: Long,
    val isDeleted: Boolean,
    val createdAt: Long
)