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

fun FuelConsumptionEntity.toExternal(distance: Int?) = FuelConsumption(
    id = id,
    amount = amount,
    price = price,
    currency = currency,
    type = type,
    carName = carName,
    odometer = odometer,
    distance = distance,
    date = date,
    isDeleted = isDeleted,
    createdAt = createdAt
)