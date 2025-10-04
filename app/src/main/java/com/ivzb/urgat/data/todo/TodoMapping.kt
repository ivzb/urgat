package com.ivzb.urgat.data.todo

fun Todo.toLocal() = LocalTodo(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
)

fun List<Todo>.toLocal() = map(Todo::toLocal)

fun LocalTodo.toExternal() = Todo(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
)

@JvmName("localToExternal")
fun List<LocalTodo>.toExternal() = map(LocalTodo::toExternal)