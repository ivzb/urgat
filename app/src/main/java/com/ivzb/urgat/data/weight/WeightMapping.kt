package com.ivzb.urgat.data.weight

fun Weight.toEntity() = WeightEntity(
    id = id,
    value = value,
    date = date,
    isDeleted = isDeleted,
    createdAt = createdAt
)

fun List<Weight>.toEntity() = map(Weight::toEntity)

fun WeightEntity.toExternal() = Weight(
    id = id,
    value = value,
    date = date,
    isDeleted = isDeleted,
    createdAt = createdAt
)

@JvmName("entityToExternal")
fun List<WeightEntity>.toExternal() = map(WeightEntity::toExternal)