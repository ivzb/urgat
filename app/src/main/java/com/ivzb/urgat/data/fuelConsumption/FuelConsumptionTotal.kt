package com.ivzb.urgat.data.fuelConsumption

import java.math.BigDecimal

data class FuelConsumptionTotal(
    val type: String,
    val amount: BigDecimal,
)