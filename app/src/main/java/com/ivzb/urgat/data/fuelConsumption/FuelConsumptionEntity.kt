package com.ivzb.urgat.data.fuelConsumption

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(
    tableName = "fuel_consumptions",
    indices = [
        Index(value = ["isDeleted", "date"]),
    ]
)
data class FuelConsumptionEntity(
    @PrimaryKey val id: String,
    val amount: BigDecimal,
    val price: BigDecimal,
    val currency: String,
    val carName: String,
    val odometer: Int,
    val date: Long,
    val isDeleted: Boolean,
    val createdAt: Long
)

