package com.ivzb.urgat.data.todo

data class Todo(
    val id: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val isDeleted: Boolean,
    val createdAt: Long,
) {

    val isActive
        get() = !isCompleted && !isDeleted

    fun formatCsv(): String =
        "\"$id\", \"$title\", \"$description\", $isCompleted, $createdAt"

}