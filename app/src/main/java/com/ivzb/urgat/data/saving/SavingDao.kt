package com.ivzb.urgat.data.saving

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingDao {

    @Query("" +
            "SELECT * FROM savings " +
            "WHERE isDeleted = 0 AND date BETWEEN :dateStart AND :dateEnd " +
            "ORDER BY date DESC " +
            "LIMIT :limit"
    )
    fun observeAll(limit: Int, dateStart: Long, dateEnd: Long): Flow<List<SavingEntity>>

    @Query("" +
            "SELECT * FROM savings " +
            "WHERE isDeleted = 0 AND date BETWEEN :dateStart AND :dateEnd AND (" +
                "name LIKE '%' || :searchQuery || '%' OR " +
                "amount LIKE '%' || :searchQuery || '%'" +
            ")" +
            "ORDER BY date DESC " +
            "LIMIT :limit"
    )
    fun observeAll(limit: Int, dateStart: Long, dateEnd: Long, searchQuery: String): Flow<List<SavingEntity>>

    @Query("SELECT * FROM savings WHERE isDeleted = 0 ORDER BY date DESC")
    fun stream(): Cursor

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SavingEntity>)

    @Upsert
    suspend fun upsert(entity: SavingEntity)

    @Query("SELECT * FROM savings WHERE id = :id")
    suspend fun getById(id: String): SavingEntity?

    @Query("DELETE FROM savings WHERE id = :id")
    suspend fun deleteById(id: String): Int

}
