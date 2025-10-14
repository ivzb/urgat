package com.ivzb.urgat.data.todo

fun Todo.toLocal() = LocalTodo(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
    isDeleted = isDeleted,
    createdAt = createdAt
)

fun List<Todo>.toLocal() = map(Todo::toLocal)

fun LocalTodo.toExternal() = Todo(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
    isDeleted = isDeleted,
    createdAt = createdAt
)

@JvmName("localToExternal")
fun List<LocalTodo>.toExternal() = map(LocalTodo::toExternal)