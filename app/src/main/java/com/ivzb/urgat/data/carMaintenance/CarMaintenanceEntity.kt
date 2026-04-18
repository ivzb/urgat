package com.ivzb.urgat.data.carMaintenance

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "car_maintenance",
    indices = [
        Index(value = ["isDeleted", "date"]),
    ]
)
data class CarMaintenanceEntity(
    @PrimaryKey val id: String,
    val description: String,
    val partNumber: String?,
    val url: String?,
    val carName: String,
    val odometer: Int,
    val date: Long,
    val isDeleted: Boolean,
    val createdAt: Long
)

