package com.ivzb.urgat.data.todo

data class Todo(
    val id: String,
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val isDeleted: Boolean = false,
    val createdAt: Long = 0
) {

    val isActive
        get() = !isCompleted && !isDeleted

}