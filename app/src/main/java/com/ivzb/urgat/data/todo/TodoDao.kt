package com.ivzb.urgat.data.todo

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Upsert
import androidx.sqlite.db.SupportSQLiteQuery
import com.ivzb.urgat.data.base.SearchQueryBuilder
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

    @RawQuery(observedEntities = [TodoEntity::class])
    fun observeAllRaw(query: SupportSQLiteQuery): Flow<List<TodoEntity>>

    fun observeAll(limit: Int, dateStart: Long, dateEnd: Long, searchQuery: String): Flow<List<TodoEntity>> {
        return observeAllRaw(
            SearchQueryBuilder.build(
                tableName = "todo",
                searchColumns = listOf("content"),
                dateColumn = "createdAt",
                orderBy = "createdAt DESC",
                limit = limit,
                dateStart = dateStart,
                dateEnd = dateEnd,
                searchQuery = searchQuery,
            )
        )
    }

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