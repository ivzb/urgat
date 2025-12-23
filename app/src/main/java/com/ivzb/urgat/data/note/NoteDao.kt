package com.ivzb.urgat.data.note

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes WHERE isDeleted = 0 ORDER BY createdAt DESC LIMIT :limit")
    fun observeAll(limit: Int): Flow<List<NoteEntity>>

    @Query("" +
            "SELECT * FROM notes " +
            "WHERE isDeleted = 0 AND content LIKE '%' || :searchQuery || '%'" +
            "ORDER BY createdAt DESC " +
            "LIMIT :limit"
    )
    fun observeAll(limit: Int, searchQuery: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE isDeleted = 0 ORDER BY createdAt DESC")
    fun streamAll(): Cursor

    @Upsert
    suspend fun upsert(entity: NoteEntity)

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getById(id: String): NoteEntity?

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteById(id: String): Int

}
