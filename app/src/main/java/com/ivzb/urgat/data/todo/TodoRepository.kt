package com.ivzb.urgat.data.todo

import kotlinx.coroutines.flow.Flow

interface TodoRepository {

    fun getStream(): Flow<List<Todo>>

    fun getStream(searchQuery: String): Flow<List<Todo>>

    fun getStream(limit: Int): Flow<List<Todo>>

    fun getStream(limit: Int, searchQuery: String): Flow<List<Todo>>

    suspend fun getAll(): List<Todo>

    fun getStreamById(id: String): Flow<Todo?>

    suspend fun get(id: String): Todo?

    suspend fun create(title: String, description: String): String

    suspend fun update(id: String, title: String, description: String)

    suspend fun complete(id: String)

    suspend fun activate(id: String)

    suspend fun clearCompleted()

    suspend fun deleteAll()

    suspend fun delete(id: String)
}
