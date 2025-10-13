package com.ivzb.urgat.data.todo

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Query("SELECT * FROM todo ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<LocalTodo>>

    @Query("" +
            "SELECT * FROM todo " +
            "WHERE title LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%'" +
            "ORDER BY createdAt DESC")
    fun observeAll(searchQuery: String): Flow<List<LocalTodo>>

    @Query("SELECT * FROM todo ORDER BY createdAt DESC LIMIT :limit")
    fun observeAll(limit: Int): Flow<List<LocalTodo>>

    @Query("" +
            "SELECT * FROM todo " +
            "WHERE title LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%'" +
            "ORDER BY createdAt DESC " +
            "LIMIT :limit"
    )
    fun observeAll(limit: Int, searchQuery: String): Flow<List<LocalTodo>>

    @Query("SELECT * FROM todo WHERE id = :todoId")
    fun observeById(todoId: String): Flow<LocalTodo>

    @Query("SELECT * FROM todo ORDER BY createdAt DESC")
    suspend fun getAll(): List<LocalTodo>

    @Query("SELECT * FROM todo WHERE id = :todoId")
    suspend fun getById(todoId: String): LocalTodo?

    @Upsert
    suspend fun upsert(todo: LocalTodo)

    @Upsert
    suspend fun upsertAll(todos: List<LocalTodo>)

    @Query("UPDATE todo SET isCompleted = :completed WHERE id = :todoId")
    suspend fun updateCompleted(todoId: String, completed: Boolean)

    @Query("DELETE FROM todo WHERE id = :todoId")
    suspend fun deleteById(todoId: String): Int

    @Query("DELETE FROM todo")
    suspend fun deleteAll()

    @Query("DELETE FROM todo WHERE isCompleted = 1")
    suspend fun deleteCompleted(): Int
}