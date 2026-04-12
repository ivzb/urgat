package com.ivzb.urgat.data.fuelConsumption

import com.ivzb.urgat.data.base.BaseRepository
import java.math.BigDecimal

interface FuelConsumptionRepository: BaseRepository<FuelConsumption> {

    suspend fun create(
        amount: BigDecimal,
        price: BigDecimal,
        currency: String,
        carName: String,
        odometer: Int,
        date: Long,
    ): String

    suspend fun update(
        id: String,
        amount: BigDecimal,
        price: BigDecimal,
        currency: String,
        carName: String,
        odometer: Int,
        date: Long,
    )

}

