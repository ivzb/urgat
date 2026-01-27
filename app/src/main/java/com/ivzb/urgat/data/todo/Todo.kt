package com.ivzb.urgat.data.todo

data class Todo(
    val id: String,
    val content: String,
    val isCompleted: Boolean,
    val isDeleted: Boolean,
    val createdAt: Long,
) {

    val isActive
        get() = !isCompleted && !isDeleted

    fun formatCsv(): String =
        "\"$id\", \"$content\", $isCompleted, $createdAt"

}