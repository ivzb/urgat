package com.ivzb.urgat.data.todo

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Query("SELECT * FROM todo WHERE isDeleted = 0 ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<TodoEntity>>

    @Query("" +
            "SELECT * FROM todo " +
            "WHERE isDeleted = 0 AND title LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%'" +
            "ORDER BY createdAt DESC")
    fun observeAll(searchQuery: String): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todo WHERE isDeleted = 0 ORDER BY createdAt DESC LIMIT :limit")
    fun observeAll(limit: Int): Flow<List<TodoEntity>>

    @Query("" +
            "SELECT * FROM todo " +
            "WHERE isDeleted = 0 AND title LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%'" +
            "ORDER BY createdAt DESC " +
            "LIMIT :limit"
    )
    fun observeAll(limit: Int, searchQuery: String): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todo WHERE id = :id")
    fun observeById(id: String): Flow<TodoEntity>

    @Query("SELECT * FROM todo WHERE isDeleted = 0 ORDER BY createdAt DESC")
    suspend fun getAll(): List<TodoEntity>

    @Query("SELECT * FROM todo WHERE id = :id")
    suspend fun getById(id: String): TodoEntity?

    @Upsert
    suspend fun upsert(todo: TodoEntity)

    @Upsert
    suspend fun upsertAll(todos: List<TodoEntity>)

    @Query("UPDATE todo SET isCompleted = :completed WHERE id = :id")
    suspend fun updateCompleted(id: String, completed: Boolean)

    @Query("DELETE FROM todo WHERE id = :id")
    suspend fun deleteById(id: String): Int

    @Query("DELETE FROM todo")
    suspend fun deleteAll()

    @Query("UPDATE todo SET isDeleted = 1 WHERE isCompleted = 1 AND isDeleted = 0")
    suspend fun deleteCompleted(): Int
}