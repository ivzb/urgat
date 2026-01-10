package com.ivzb.urgat.data.note

data class Note(
    val id: String,
    val content: String,
    val isDeleted: Boolean,
    val createdAt: Long,
) {

    fun formatCsv(): String =
        "\"$id\", \"$content\", $createdAt"

}
