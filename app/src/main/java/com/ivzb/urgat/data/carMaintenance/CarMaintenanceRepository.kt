package com.ivzb.urgat.data.carMaintenance

import com.ivzb.urgat.data.base.BaseRepository

interface CarMaintenanceRepository: BaseRepository<CarMaintenance> {

    suspend fun create(
        description: String,
        partNumber: String?,
        url: String?,
        carName: String,
        odometer: Int,
        date: Long,
    ): String

    suspend fun update(
        id: String,
        description: String,
        partNumber: String?,
        url: String?,
        carName: String,
        odometer: Int,
        date: Long,
    )

    suspend fun getLastUsedCarName(): String?

}

