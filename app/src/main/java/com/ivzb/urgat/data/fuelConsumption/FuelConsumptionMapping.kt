package com.ivzb.urgat.data.fuelConsumption

fun FuelConsumption.toEntity() = FuelConsumptionEntity(
    id = id,
    amount = amount,
    price = price,
    currency = currency,
    type = type,
    carName = carName,
    odometer = odometer,
    date = date,
    isDeleted = isDeleted,
    createdAt = createdAt
)

fun List<FuelConsumption>.toEntity() = map(FuelConsumption::toEntity)

fun FuelConsumptionEntity.toExternal() = FuelConsumption(
    id = id,
    amount = amount,
    price = price,
    currency = currency,
    type = type,
    carName = carName,
    odometer = odometer,
    date = date,
    isDeleted = isDeleted,
    createdAt = createdAt
)

@JvmName("entityToExternal")
fun List<FuelConsumptionEntity>.toExternal() = map(FuelConsumptionEntity::toExternal)

