package com.ivzb.urgat.data.note

fun Note.toEntity() = NoteEntity(
    id = id,
    content = content,
    isDeleted = isDeleted,
    createdAt = createdAt
)

fun List<Note>.toEntity() = map(Note::toEntity)

fun NoteEntity.toExternal() = Note(
    id = id,
    content = content,
    isDeleted = isDeleted,
    createdAt = createdAt
)

@JvmName("entityToExternal")
fun List<NoteEntity>.toExternal() = map(NoteEntity::toExternal)
