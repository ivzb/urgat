package com.ivzb.urgat.data.note

import kotlinx.coroutines.flow.Flow

interface NotesRepository {

    fun getStream(): Flow<List<Note>>

    fun getStream(searchQuery: String): Flow<List<Note>>

    fun getStream(limit: Int): Flow<List<Note>>

    fun getStream(limit: Int, searchQuery: String): Flow<List<Note>>

    suspend fun create(content: String): String

    suspend fun update(id: String, content: String)

    suspend fun get(id: String): Note?

}
