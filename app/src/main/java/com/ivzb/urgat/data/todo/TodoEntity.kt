package com.ivzb.urgat.data.todo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo")
data class TodoEntity(
    @PrimaryKey val id: String,
    val content: String,
    val isCompleted: Boolean,
    val isDeleted: Boolean,
    val createdAt: Long
)