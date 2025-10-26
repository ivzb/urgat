package com.ivzb.urgat.data.note

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String,
    val content: String,
    var isDeleted: Boolean,
    var createdAt: Long
)
