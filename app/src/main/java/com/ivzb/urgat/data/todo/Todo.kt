package com.ivzb.urgat.data.todo

data class Todo(
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val id: String,
) {

    val isActive
        get() = !isCompleted

}