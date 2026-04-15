package com.ivzb.urgat.data.fuelConsumption

import androidx.room.Embedded

data class FuelConsumptionEntityWithDistance(
    @Embedded val entity: FuelConsumptionEntity,
    val distance: Int?,
)

