package com.ivzb.urgat.data.todo

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Query("" +
            "SELECT * FROM todo " +
            "WHERE isDeleted = 0 AND createdAt BETWEEN :dateStart AND :dateEnd " +
            "ORDER BY createdAt DESC " +
            "LIMIT :limit"
    )
    fun observeAll(limit: Int, dateStart: Long, dateEnd: Long): Flow<List<TodoEntity>>

    @Query("" +
            "SELECT * FROM todo " +
            "WHERE isDeleted = 0 AND createdAt BETWEEN :dateStart AND :dateEnd AND content LIKE '%' || :searchQuery || '%'" +
            "ORDER BY createdAt DESC " +
            "LIMIT :limit"
    )
    fun observeAll(limit: Int, dateStart: Long, dateEnd: Long, searchQuery: String): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todo WHERE isDeleted = 0 ORDER BY createdAt DESC")
    fun stream(): Cursor

    @Query("SELECT * FROM todo WHERE id = :id")
    suspend fun getById(id: String): TodoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<TodoEntity>)

    @Upsert
    suspend fun upsert(entity: TodoEntity)

    @Query("UPDATE todo SET isCompleted = :completed WHERE id = :id")
    suspend fun updateCompleted(id: String, completed: Boolean)

    @Query("UPDATE todo SET isDeleted = 1 WHERE isCompleted = 1 AND isDeleted = 0")
    suspend fun deleteCompleted(): Int

    @Query("DELETE FROM todo WHERE id = :id")
    suspend fun deleteById(id: String): Int
}