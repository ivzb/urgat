package com.ivzb.urgat.data.todo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "todo"
)
data class LocalTodo(
    @PrimaryKey val id: String,
    var title: String,
    var description: String,
    var isCompleted: Boolean,
    var isDeleted: Boolean,
    var createdAt: Long
)