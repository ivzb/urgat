package com.ivzb.urgat.data.carMaintenance

fun CarMaintenance.toEntity() = CarMaintenanceEntity(
    id = id,
    description = description,
    partNumber = partNumber,
    url = url,
    carName = carName,
    odometer = odometer,
    date = date,
    isDeleted = isDeleted,
    createdAt = createdAt
)

fun CarMaintenanceEntity.toExternal() = CarMaintenance(
    id = id,
    description = description,
    partNumber = partNumber,
    url = url,
    carName = carName,
    odometer = odometer,
    date = date,
    isDeleted = isDeleted,
    createdAt = createdAt
)

