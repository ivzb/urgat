package com.ivzb.urgat.data.todo

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Upsert
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Query("SELECT * FROM todo WHERE isDeleted = 0 ORDER BY createdAt DESC LIMIT :limit")
    fun observeAll(limit: Int): Flow<List<TodoEntity>>

    @Query("" +
            "SELECT * FROM todo " +
            "WHERE isDeleted = 0 AND title LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%'" +
            "ORDER BY createdAt DESC " +
            "LIMIT :limit"
    )
    fun observeAll(limit: Int, searchQuery: String): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todo WHERE isDeleted = 0 ORDER BY createdAt DESC")
    fun streamAll(): Cursor

    @Query("SELECT * FROM todo WHERE id = :id")
    suspend fun getById(id: String): TodoEntity?

    @Upsert
    suspend fun upsert(entity: TodoEntity)

    @Query("UPDATE todo SET isCompleted = :completed WHERE id = :id")
    suspend fun updateCompleted(id: String, completed: Boolean)

    @Query("UPDATE todo SET isDeleted = 1 WHERE isCompleted = 1 AND isDeleted = 0")
    suspend fun deleteCompleted(): Int

    @Query("DELETE FROM todo WHERE id = :id")
    suspend fun deleteById(id: String): Int
}