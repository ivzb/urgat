package com.ivzb.urgat.data.todo

fun Todo.toEntity() = TodoEntity(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
    isDeleted = isDeleted,
    createdAt = createdAt
)

fun List<Todo>.toEntity() = map(Todo::toEntity)

fun TodoEntity.toExternal() = Todo(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
    isDeleted = isDeleted,
    createdAt = createdAt
)

@JvmName("entityToExternal")
fun List<TodoEntity>.toExternal() = map(TodoEntity::toExternal)