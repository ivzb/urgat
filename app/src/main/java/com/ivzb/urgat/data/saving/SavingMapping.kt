package com.ivzb.urgat.data.saving

fun Saving.toEntity() = SavingEntity(
    id = id,
    name = name,
    amount = amount,
    date = date,
    isDeleted = isDeleted,
    createdAt = createdAt
)

fun List<Saving>.toEntity() = map(Saving::toEntity)

fun SavingEntity.toExternal() = Saving(
    id = id,
    name = name,
    amount = amount,
    date = date,
    isDeleted = isDeleted,
    createdAt = createdAt
)

@JvmName("entityToExternal")
fun List<SavingEntity>.toExternal() = map(SavingEntity::toExternal)