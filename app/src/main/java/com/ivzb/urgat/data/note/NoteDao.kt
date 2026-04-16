package com.ivzb.urgat.data.note

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
interface NoteDao {

    @Query("" +
            "SELECT * FROM notes " +
            "WHERE isDeleted = 0 AND createdAt BETWEEN :dateStart AND :dateEnd " +
            "ORDER BY createdAt DESC " +
            "LIMIT :limit"
    )
    fun observeAll(limit: Int, dateStart: Long, dateEnd: Long): Flow<List<NoteEntity>>

    @RawQuery(observedEntities = [NoteEntity::class])
    fun observeAllRaw(query: SupportSQLiteQuery): Flow<List<NoteEntity>>

    fun observeAll(limit: Int, dateStart: Long, dateEnd: Long, searchQuery: String): Flow<List<NoteEntity>> {
        return observeAllRaw(
            SearchQueryBuilder.build(
                tableName = "notes",
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

    @Query("SELECT * FROM notes WHERE isDeleted = 0 ORDER BY createdAt DESC")
    fun stream(): Cursor

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<NoteEntity>)

    @Upsert
    suspend fun upsert(entity: NoteEntity)

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getById(id: String): NoteEntity?

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteById(id: String): Int

}
