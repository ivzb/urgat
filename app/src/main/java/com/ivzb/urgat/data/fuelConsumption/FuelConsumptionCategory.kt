package com.ivzb.urgat.data.fuelConsumption

data class FuelConsumptionCategory(
    val id: String,
    val name: String,
    val totals: List<FuelConsumptionTotal>,

    val fuelConsumption: List<FuelConsumption>,
)